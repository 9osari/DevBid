package org.devbid.user.dto;

import lombok.Builder;
import lombok.Getter;
import org.devbid.user.domain.User;

import java.util.List;

@Getter
@Builder
public class MyPageData {
    private final User user;
    private final int auctionActiveCount;
    private final int auctionCount;
    private final int productCount;
    private final int participatingAuctionCount;
    private final List<RecentAuctionDto> recentAuctions;
    private final List<RecentProductDto> recentProducts;
    private final List<RecentBidDto> recentBids;
    private final List<RecentBuyOutDto> recentBuyouts;
}
