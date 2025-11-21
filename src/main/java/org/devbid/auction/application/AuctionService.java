package org.devbid.auction.application;


import org.devbid.auction.domain.Auction;
import org.devbid.auction.dto.AuctionListResponse;
import org.devbid.auction.dto.AuctionRegistrationRequest;
import org.devbid.auction.dto.BidPlacedEvent;
import org.devbid.auction.dto.BuyOutEvent;

import java.math.BigDecimal;
import java.util.List;

public interface AuctionService {
    void registerAuction(AuctionRegistrationRequest auction, Long sellerId);

    List<AuctionListResponse> findAllAuctions();

    Auction getAuctionById(Long auctionId);

    BidPlacedEvent placeBid(Long auctionId, Long bidderId, BigDecimal bidAmount);

    BuyOutEvent buyOut(Long auctionId, Long buyerId);


}
