package org.devbid.home.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.auction.domain.Auction;
import org.devbid.auction.domain.AuctionStatus;
import org.devbid.auction.repository.AuctionRepository;
import org.devbid.auction.repository.BidRepository;
import org.devbid.home.dto.HomeData;
import org.devbid.home.dto.HotAuctionDto;
import org.devbid.home.dto.RecentAuctionDto;
import org.devbid.product.application.awsService.S3Service;
import org.devbid.product.domain.Product;
import org.devbid.product.domain.ProductImage;
import org.devbid.product.domain.ProductStatus;
import org.devbid.product.repository.ProductRepository;
import org.devbid.user.domain.UserStatus;
import org.devbid.user.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HomeApplicationService {
    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;
    private final ProductRepository productRepository;
    private final BidRepository bidRepository;
    private final S3Service s3Service;

    public HomeData getHomeData() {
        int totalOngoingAuctions = auctionRepository.countByStatus(AuctionStatus.ONGOING);
        int totalProducts = productRepository.countBySaleStatusNot(ProductStatus.DELETED);
        int todayDeals = bidRepository.countTodayTrades();
        long userCount = userRepository.countByStatus(UserStatus.ACTIVE);
        List<Auction> hotAuction = auctionRepository.findHotAuction();
        List<Auction> recentAuction = auctionRepository.findAllByStatusNot(AuctionStatus.CANCELLED);

        List<RecentAuctionDto> recentAuctions = recentAuction.stream()
                .map(this::toRecentAuctionDto)
                .toList();

        List<HotAuctionDto> hotAuctions = hotAuction.stream()
                .map(this::toHotAuctionDto)
                .toList();

        return HomeData.builder()
                .totalOngoingAuctions(totalOngoingAuctions)
                .totalProducts(totalProducts)
                .todayDeals(todayDeals)
                .userCount(userCount)
                .hotAuctions(hotAuctions)
                .recentAuctions(recentAuctions)
                .build();
    }

    private RecentAuctionDto toRecentAuctionDto(Auction auction) {
        Product product = auction.getProduct();
        return new RecentAuctionDto(
                auction.getId(),
                product.getProductName().getValue(),
                getMainImage(product),
                getSubImageUrl(product),
                auction.getStartingPrice().getValue(),
                auction.getBuyoutPrice().getValue(),
                auction.getStartTime(),
                auction.getEndTime()
        );
    }

    private HotAuctionDto toHotAuctionDto(Auction auction) {
        Product product = auction.getProduct();
        return new HotAuctionDto(
                auction.getId(),
                product.getProductName().getValue(),
                getMainImage(product),
                getSubImageUrl(product),
                auction.getCurrentPrice().getValue(),
                auction.getStartTime(),
                auction.getEndTime(),
                auction.getBidCount(),
                auction.getBuyoutPrice().getValue()
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
