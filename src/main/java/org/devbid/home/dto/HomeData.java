package org.devbid.home.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class HomeData {
    private final int totalOngoingAuctions;
    private final int totalProducts;
    private final long userCount;
    private final int todayDeals;
    private final List<HotAuctionDto> hotAuctions;
    private final List<RecentAuctionDto> recentAuctions;
}
