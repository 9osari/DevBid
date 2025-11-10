package org.devbid.auction.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.devbid.infrastructure.common.BaseEntity;
import org.devbid.product.domain.Product;
import org.devbid.user.domain.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "auctions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Auction extends BaseEntity {
    @Version
    private Long version;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Embedded
    private StartingPrice startingPrice;

    @Embedded
    private BuyoutPrice buyoutPrice;

    @Embedded
    private CurrentPrice currentPrice;

    @Column(name = "current_bidder_id")
    private Long currentBidderId;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "extended_count" , nullable = false)
    private int extendedCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "auction_status", nullable = false)
    private AuctionStatus status = AuctionStatus.BEFORE_START;

    @Column(name = "bid_count", nullable = false)
    private int bidCount = 0;


    public static Auction of(Product product,
                             StartingPrice startingPrice,
                             BuyoutPrice buyoutPrice,
                             CurrentPrice currentPrice,
                             LocalDateTime startTime,
                             LocalDateTime endTime,
                             AuctionStatus status) {
        Auction auction = new Auction();
        auction.product = product;
        auction.startingPrice = startingPrice;
        auction.buyoutPrice = buyoutPrice;
        auction.currentPrice = currentPrice;
        auction.startTime = startTime;
        auction.endTime = endTime;
        auction.status = status;
        return auction;
    }


    //입찰
    public Bid placeBid(User bidder, BidAmount bidAmount) {
        //검증로직
        if(this.status != AuctionStatus.ONGOING) {
            throw new IllegalStateException("진행 중인 경매가 아닙니다.");
        }
        if(LocalDateTime.now().isAfter(this.endTime)) {
            throw new IllegalStateException("경매가 종료되었습니다.");
        }
        if(this.currentPrice != null && bidAmount.getValue().compareTo(this.currentPrice.getValue()) <= 0) {
            throw new IllegalStateException("현재가 보다 높은 금액을 입찰해주세요.");
        }
        if(this.product.getSeller().getId().equals(bidder.getId())) {
            throw new IllegalStateException("판매자는 입찰 할 수 없습니다.");
        }

        this.currentPrice = new CurrentPrice(bidAmount.getValue());
        this.currentBidderId = bidder.getId();
        this.bidCount++;
        return Bid.of(this, bidder, bidAmount); //새 입찰 생성
    }
}
