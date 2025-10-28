package org.devbid.auction.application;


import org.devbid.auction.dto.AuctionRegistrationRequest;

public interface AuctionService {
    void registerAuction(AuctionRegistrationRequest auction, Long sellerId);
}
