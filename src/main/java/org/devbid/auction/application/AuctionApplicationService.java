package org.devbid.auction.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.auction.domain.Auction;
import org.devbid.auction.domain.AuctionFactory;
import org.devbid.auction.dto.AuctionListResponse;
import org.devbid.auction.dto.AuctionRegistrationRequest;
import org.devbid.auction.dto.BidPlacedEvent;
import org.devbid.auction.dto.BuyOutEvent;
import org.devbid.auction.repository.AuctionRepository;
import org.devbid.product.application.awsService.S3Service;
import org.devbid.product.domain.Product;
import org.devbid.product.domain.ProductImage;
import org.devbid.product.repository.ProductRepository;
import org.devbid.user.repository.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionApplicationService implements AuctionService{
    private final ProductRepository productRepository;
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;
    private final AuctionDtoMapper auctionDtoMapper;
    private final S3Service s3Service;
    private final BidApplicationService bidApplicationService;
    private final AuctionLockManager auctionLockManager;

    @Override
    public void registerAuction(AuctionRegistrationRequest request,  Long sellerId) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        matchedSeller(sellerId, product);

        Auction auction = AuctionFactory.createFromPrimitives(
                product,
                request.startingPrice(),
                request.buyoutPrice(),
                request.startTime(),
                request.endTime()
        );
        auctionRepository.save(auction);
    }

    private static void matchedSeller(Long sellerId, Product product) {
        if(!product.getSeller().getId().equals(sellerId)) {
            throw new IllegalArgumentException("Seller id not matched");
        }
    }

    @Override
    public List<AuctionListResponse> findAllAuctions() {
        //여러 경매 각각에 대해 이미지 URL을 생성
        return auctionRepository.findAll().stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public Page<AuctionListResponse> findAllAuctionsById(Long id, Pageable pageable) {
        Page<Auction> auctions = auctionRepository.findBySellerId(id, pageable);
        return auctions.map(this::convertToResponse);
    }

    @Override
    public Auction getAuctionById(Long auctionId) {
        return auctionRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("auction not found"));
    }

    @Cacheable(value = "auctionDetail", key = "#auctionId")
    public AuctionListResponse getAuctionDetail(Long auctionId) {
        //캐시 없으면 DB 조회
        Auction auction = getAuctionById(auctionId);
        return convertToResponse(auction);
    }

    private AuctionListResponse convertToResponse(Auction auction) {
        String mainImageUrl = getMainImageUrl(auction);
        List<String> subImageUrls = getSubImageUrls(auction);
        String winnerNickname = getWinnerNickname(auction);
        return auctionDtoMapper.toResponse(auction, winnerNickname, mainImageUrl, subImageUrls);
    }

    private String getWinnerNickname(Auction auction) {
        String winnerNickname = null;
        if (auction.getCurrentBidderId() != null) {
            winnerNickname = userRepository.findById(auction.getCurrentBidderId())
                    .orElseThrow(() -> new IllegalArgumentException("user not found"))
                    .getNickname().getValue();
        }
        return winnerNickname;
    }

    private String getMainImageUrl(Auction auction) {
        return auction.getProduct().getImages().stream()
                .filter(img -> img.getSortOrder() == 1)
                .findFirst()
                .map(img -> s3Service.buildPublicUrl(img.getImageKey()))
                .orElse(null);
    }

    private List<String> getSubImageUrls(Auction auction) {
        return auction.getProduct().getImages().stream()
                .filter(img -> img.getSortOrder() > 1)
                .sorted(Comparator.comparing(ProductImage::getSortOrder))
                .map(img -> s3Service.buildPublicUrl(img.getImageKey()))
                .toList();
    }

    public BidPlacedEvent placeBid(Long auctionId, Long bidderId, BigDecimal bidAmount) {
        return auctionLockManager.executeWithLock(
                auctionId,
                () -> bidApplicationService.placeBid(auctionId, bidderId, bidAmount)
        );
    }

    public BuyOutEvent buyOut(Long auctionId, Long buyerId) {
        return auctionLockManager.executeWithLock(
                auctionId,
                () -> bidApplicationService.buyOut(auctionId, buyerId)
        );
    }
}
