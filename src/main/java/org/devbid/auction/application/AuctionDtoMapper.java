package org.devbid.auction.application;

import org.devbid.auction.domain.Auction;
import org.devbid.auction.dto.AuctionListResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuctionDtoMapper {
    public AuctionListResponse toResponse(Auction auction, String mainImageUrl, List<String> subImageUrls) {
        return new AuctionListResponse(
                auction.getId(),
                auction.getProduct().getProductName().getValue(),
                auction.getProduct().getSeller().getNickname().getValue(),
                auction.getStartingPrice().getValue(),
                auction.getCurrentPrice().getValue(),
                auction.getBuyoutPrice().getValue(),
                auction.getBidCount(),
                auction.getStartTime(),
                auction.getEndTime(),
                auction.getStatus(),
                auction.getProduct().getDescription().getValue(),
                mainImageUrl,
                subImageUrls
        );
    }
}
