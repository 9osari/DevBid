package org.devbid.auction.application;


import org.devbid.auction.domain.Auction;
import org.devbid.auction.dto.AuctionListResponse;
import org.devbid.auction.dto.AuctionRegistrationRequest;
import org.devbid.auction.event.BidPlacedEvent;
import org.devbid.auction.event.BuyOutEvent;

import java.math.BigDecimal;
import java.util.List;

public interface AuctionService {
    void registerAuction(AuctionRegistrationRequest auction, Long sellerId);

    List<AuctionListResponse> findAllAuctions();

    Auction findById(Long auctionId);

    BidPlacedEvent placeBid(Long auctionId, Long bidderId, BigDecimal bidAmount);

    BuyOutEvent buyOut(Long auctionId, Long buyerId);


}
