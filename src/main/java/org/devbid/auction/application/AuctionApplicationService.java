package org.devbid.auction.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.auction.domain.Auction;
import org.devbid.auction.domain.AuctionFactory;
import org.devbid.auction.dto.AuctionListResponse;
import org.devbid.auction.dto.AuctionRegistrationRequest;
import org.devbid.auction.repository.AuctionRepository;
import org.devbid.product.application.awsService.S3Service;
import org.devbid.product.domain.Product;
import org.devbid.product.domain.ProductImage;
import org.devbid.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuctionApplicationService implements AuctionService{
    private final ProductRepository productRepository;
    private final AuctionRepository auctionRepository;
    private final AuctionDtoMapper auctionDtoMapper;
    private final S3Service s3Service;

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
        return auctionDtoMapper.toResponse(auction, mainImageUrl, subImageUrls);
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

}
