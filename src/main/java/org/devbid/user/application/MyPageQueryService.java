package org.devbid.user.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.auction.domain.Auction;
import org.devbid.auction.domain.AuctionStatus;
import org.devbid.auction.domain.Bid;
import org.devbid.auction.repository.AuctionRepository;
import org.devbid.auction.repository.BidRepository;
import org.devbid.product.application.awsService.S3Service;
import org.devbid.product.domain.Product;
import org.devbid.product.domain.ProductImage;
import org.devbid.product.repository.ProductRepository;
import org.devbid.user.domain.User;
import org.devbid.user.dto.MyPageData;
import org.devbid.user.dto.RecentAuctionDto;
import org.devbid.user.dto.RecentBidDto;
import org.devbid.user.dto.RecentBuyOutDto;
import org.devbid.user.dto.RecentProductDto;
import org.devbid.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MyPageQueryService {
    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;
    private final ProductRepository productRepository;
    private final BidRepository bidRepository;
    private final S3Service s3Service;

    public MyPageData getMyPageData(Long userId, Pageable auctionPageable, Pageable productPageable,
                                     Pageable bidPageable, Pageable buyoutPageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User id not found: " + userId));
        Page<Auction> auctions = auctionRepository.findRecentBySellerId(userId, auctionPageable);
        Page<Product> products = productRepository.findRecentProduct(userId, productPageable);
        Page<Bid> bids = bidRepository.findRecentByBidderId(userId, bidPageable);
        Page<Bid> buyouts = bidRepository.findRecentBuyOutByUserId(userId, buyoutPageable);

        // 디자인 패턴 = 목적
        // 퍼사드의 목적 => 인터페이스를 활용 => 구체적인 로직을 감추고 추상화된 메시지로 제공

        List<RecentAuctionDto> recentAuctions = auctions.stream()
                .map(this::toRecentAuctionDto)
                .toList();

        List<RecentProductDto> recentProducts = products.stream()
                .map(this::toRecentProductDto)
                .toList();

        List<RecentBidDto> recentBids = bids.stream()
                .map(this::toRecentBidDto)
                .toList();

        List<RecentBuyOutDto> recentBuyOuts = buyouts.stream()
                .map(this::toRecentBuyOutDto)
                .toList();

        return MyPageData.builder()
                .user(user)
                .auctionActiveCount(auctionRepository.countByProductSellerIdAndStatus(userId, AuctionStatus.ONGOING))
                .auctionCount(auctionRepository.countByProductSellerId(userId))
                .productCount(productRepository.countBySellerId(userId))
                .participatingAuctionCount(bidRepository.countByBidderId(userId))
                .recentAuctions(recentAuctions)
                .recentProducts(recentProducts)
                .recentBids(recentBids)
                .recentBuyouts(recentBuyOuts)
                // 섹션 페이징
                .auctionCurrentPage(auctions.getNumber())
                .auctionTotalPages(auctions.getTotalPages())
                .auctionHasNext(auctions.hasNext())
                .productCurrentPage(products.getNumber())
                .productTotalPages(products.getTotalPages())
                .productHasNext(products.hasNext())
                .bidCurrentPage(bids.getNumber())
                .bidTotalPages(bids.getTotalPages())
                .bidHasNext(bids.hasNext())
                .buyoutCurrentPage(buyouts.getNumber())
                .buyoutTotalPages(buyouts.getTotalPages())
                .buyoutHasNext(buyouts.hasNext())
                .build();
    }

    private RecentAuctionDto toRecentAuctionDto(Auction auction) {
        Product product = auction.getProduct();
        return new RecentAuctionDto(
                auction.getId(),
                auction.getStatus(),
                auction.getCurrentPrice().getValue(),
                auction.getBidCount(),
                auction.getEndTime(),
                auction.getStartTime(),
                product.getProductName().getValue(),
                getMainImage(product),
                getSubImageUrl(product)
        );
    }

    private RecentProductDto toRecentProductDto(Product product) {
        return new RecentProductDto(
                product.getId(),
                getMainImage(product),
                getSubImageUrl(product),
                product.getProductName().getValue(),
                product.getSaleStatus(),
                product.getCondition(),
                product.getCategory().getName(),
                product.getSaleStatus().name(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                product.activeAuctionCount()
        );
    }

    private RecentBidDto toRecentBidDto(Bid bid) {
        Auction auction = bid.getAuction();
        Product product = auction.getProduct();
        return new RecentBidDto(
                auction.getId(),
                getMainImage(product),
                getSubImageUrl(product),
                product.getProductName().getValue(),
                bid.getBidAmount().getValue(),
                auction.getCurrentPrice().getValue(),
                bid.getCreatedAt()
        );
    }

    private RecentBuyOutDto toRecentBuyOutDto(Bid bid) {
        Auction auction = bid.getAuction();
        Product product = auction.getProduct();
        return new RecentBuyOutDto(
                auction.getId(),
                getMainImage(product),
                getSubImageUrl(product),
                product.getProductName().getValue(),
                bid.getBidAmount().getValue(),
                bid.getCreatedAt()
        );
    }

    private String getMainImage(Product product) {
        return product.getImages().stream()
                .filter(img -> img.getSortOrder() == 1)
                .findFirst()
                .map(img -> s3Service.buildPublicUrl(img.getImageKey()))
                .orElse(null);
    }

    private List<String> getSubImageUrl(Product product) {
        return product.getImages().stream()
                .filter(img -> img.getSortOrder() > 1)
                .sorted(Comparator.comparing(ProductImage::getSortOrder))
                .map(img -> s3Service.buildPublicUrl(img.getImageKey()))
                .toList();
    }
}
