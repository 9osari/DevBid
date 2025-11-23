package org.devbid.auction.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.auction.domain.Auction;
import org.devbid.auction.domain.Bid;
import org.devbid.auction.domain.BidAmount;
import org.devbid.auction.dto.BidPlacedEvent;
import org.devbid.auction.dto.BuyOutEvent;
import org.devbid.auction.repository.AuctionRepository;
import org.devbid.auction.repository.BidRepository;
import org.devbid.user.domain.User;
import org.devbid.user.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class BidApplicationService {
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;
    private final BidRepository bidRepository;

    @Transactional
    @CacheEvict(value = "auctionDetail", key = "#auctionId")
    public BidPlacedEvent placeBid(Long auctionId, Long bidderId, BigDecimal bidAmount) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("auction not found"));
        User bidder = userRepository.findById(bidderId) //사용자 찾기
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Bid bid = auction.placeBid(bidder, new BidAmount(bidAmount)); //엔티티에 비즈니스 로직 위임

        bidRepository.save(bid);

        return BidPlacedEvent.of(auctionId,
                bidderId,
                bid.getBidder().getNickname().getValue(),
                bidAmount,
                auction.getBidCount()
        );
    }

    @Transactional
    @CacheEvict(value = "auctionDetail", key = "#auctionId")
    public BuyOutEvent buyOut(Long auctionId, Long buyerId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("auction not found"));
        User buyer = userRepository.findById(buyerId) //사용자 찾기
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Bid bid = auction.buyOut(buyer);

        bidRepository.save(bid);

        return BuyOutEvent.of(
                auctionId,
                buyerId,
                buyer.getNickname().getValue(),
                auction.getBuyoutPrice().getValue(),
                bid.getAuction().getEndTime()
        );
    }
}
