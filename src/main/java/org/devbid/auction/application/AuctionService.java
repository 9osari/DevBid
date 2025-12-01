package org.devbid.auction.application;


import org.devbid.auction.domain.Auction;
import org.devbid.auction.dto.AuctionEditRequest;
import org.devbid.auction.dto.AuctionListResponse;
import org.devbid.auction.dto.AuctionRegistrationRequest;
import org.devbid.auction.dto.BidPlacedEvent;
import org.devbid.auction.dto.BuyOutEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface AuctionService {
    void registerAuction(AuctionRegistrationRequest auction, Long sellerId);
    void updateAuction(Long auctionId, AuctionEditRequest auction, Long sellerId);
    void deleteAuction(Long auctionId, Long sellerId);

    BidPlacedEvent placeBid(Long auctionId, Long bidderId, BigDecimal bidAmount);
    BuyOutEvent buyOut(Long auctionId, Long buyerId);

    List<AuctionListResponse> findAllAuctions();
    Page<AuctionListResponse> findAllAuctionsById(Long id, Pageable pageable);

    AuctionListResponse getAuctionDetail(Long auctionId);

    Auction getAuctionById(Long auctionId);
}
