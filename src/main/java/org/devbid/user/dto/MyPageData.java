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

    // 각 섹션별 페이징 정보
    private final int auctionCurrentPage;
    private final int auctionTotalPages;
    private final boolean auctionHasNext;

    private final int productCurrentPage;
    private final int productTotalPages;
    private final boolean productHasNext;

    private final int bidCurrentPage;
    private final int bidTotalPages;
    private final boolean bidHasNext;

    private final int buyoutCurrentPage;
    private final int buyoutTotalPages;
    private final boolean buyoutHasNext;
}
