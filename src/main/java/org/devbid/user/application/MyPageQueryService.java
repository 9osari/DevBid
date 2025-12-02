package org.devbid.user.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.auction.domain.Auction;
import org.devbid.auction.domain.AuctionStatus;
import org.devbid.auction.domain.Bid;
import org.devbid.auction.repository.AuctionRepository;
import org.devbid.auction.repository.BidRepository;
import org.devbid.product.domain.Product;
import org.devbid.product.repository.ProductRepository;
import org.devbid.user.domain.User;
import org.devbid.user.dto.MyPageData;
import org.devbid.user.dto.RecentAuctionDto;
import org.devbid.user.dto.RecentBidDto;
import org.devbid.user.dto.RecentBuyOutDto;
import org.devbid.user.dto.RecentProductDto;
import org.devbid.user.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageQueryService {
    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;
    private final ProductRepository productRepository;
    private final BidRepository bidRepository;

    public MyPageData getMyPageData(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User id not found: " + userId));
        List<Auction> auctions = auctionRepository.findRecentBySellerId(userId);
        List<Product> products = productRepository.findRecentProduct(userId);
        List<Bid> bids = bidRepository.findRecentByBidderId(userId);
        List<Bid> buyouts = bidRepository.findRecentBuyOutByUserId(userId);

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
                product.getProductName().getValue(),
                product.getMainImageUrl(),
                product.getSubImageUrls()
        );
    }

    private RecentProductDto toRecentProductDto(Product product) {
        return new RecentProductDto(
                product.getId(),
                product.getMainImageUrl(),
                product.getSubImageUrls(),
                product.getProductName().getValue(),
                product.getSaleStatus(),
                product.getCondition(),
                product.getCategory().getName(),
                product.getSaleStatus().name(),
                product.activeAuctionCount()
        );
    }

    private RecentBidDto toRecentBidDto(Bid bid) {
        Auction auction = bid.getAuction();
        Product product = auction.getProduct();
        return new RecentBidDto(
                auction.getId(),
                product.getMainImageUrl(),
                product.getSubImageUrls(),
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
                product.getMainImageUrl(),
                product.getSubImageUrls(),
                product.getProductName().getValue(),
                bid.getBidAmount().getValue(),
                bid.getCreatedAt()
        );
    }
}
