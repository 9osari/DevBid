package org.devbid.auction.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.auction.dto.BidPlacedEvent;
import org.devbid.auction.dto.BuyOutEvent;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionFacade {
    private final AuctionApplicationService auctionApplicationService;
    private final AuctionLockManager auctionLockManager;

    public BidPlacedEvent placeBid(Long auctionId, Long bidderId, BigDecimal bidAmount) {
        return auctionLockManager.executeWithLock(
                auctionId,
                () -> auctionApplicationService.placeBid(auctionId, bidderId, bidAmount)
        );
    }

    public BuyOutEvent buyOut(Long auctionId, Long buyerId) {
        return auctionLockManager.executeWithLock(
                auctionId,
                () -> auctionApplicationService.buyOut(auctionId, buyerId)
        );
    }
}
