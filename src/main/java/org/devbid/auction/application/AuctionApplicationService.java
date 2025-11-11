package org.devbid.auction.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.auction.domain.Auction;
import org.devbid.auction.domain.AuctionFactory;
import org.devbid.auction.domain.Bid;
import org.devbid.auction.domain.BidAmount;
import org.devbid.auction.dto.AuctionListResponse;
import org.devbid.auction.dto.AuctionRegistrationRequest;
import org.devbid.auction.event.BidPlacedEvent;
import org.devbid.auction.repository.AuctionRepository;
import org.devbid.auction.repository.BidRepository;
import org.devbid.product.application.awsService.S3Service;
import org.devbid.product.domain.Product;
import org.devbid.product.domain.ProductImage;
import org.devbid.product.repository.ProductRepository;
import org.devbid.user.domain.User;
import org.devbid.user.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final BidRepository bidRepository;
    private final AuctionDtoMapper auctionDtoMapper;
    private final S3Service s3Service;
    private final ApplicationEventPublisher eventPublisher;

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
    public Auction findById(Long auctionId) {
        return auctionRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("auction not found"));
    }

    public AuctionListResponse getAuctionDetail(Long auctionId) {
        Auction auction = findById(auctionId);
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

    @Override
    @Transactional
    public void placeBid(Long auctionId, Long bidderId, BigDecimal bidAmount) {
        Auction auction = findById(auctionId);  //경매찾기
        User bidder = userRepository.findById(bidderId) //사용자 찾기
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        try {
            //도메인 객체에 비즈니스 로직 위임
            Bid bid = auction.placeBid(bidder, new BidAmount(bidAmount));
            bidRepository.save(bid);

            //웹소켓 이벤트 발행
            BidPlacedEvent event = BidPlacedEvent.of(auctionId, bidderId, bidAmount);
            eventPublisher.publishEvent(event);
            log.info("입찰 이벤트 - event: {}", event);
        } catch (OptimisticLockingFailureException e) {
            log.error("낙관적 락 충돌 auctionId: {}", auctionId, e);
            throw new IllegalArgumentException("다른 입찰이 먼저 처리되었습니다.");
        }
    }

}
