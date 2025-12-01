package org.devbid.auction.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.devbid.infrastructure.common.BaseEntity;
import org.devbid.product.domain.Product;
import org.devbid.user.domain.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "auctions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Auction extends BaseEntity {
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

    public boolean updateAuction(
            BigDecimal startingPrice,
            BigDecimal buyoutPrice,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        boolean isUpdated = false;

        if(this.bidCount > 0) {
            throw new IllegalStateException("입찰이 진행 중인 경매는 수정할 수 없습니다.");
        }

        if(startingPrice != null) {
            this.startingPrice = StartingPrice.from(startingPrice);
            this.currentPrice = CurrentPrice.from(startingPrice);
            isUpdated = true;
        }

        if(buyoutPrice != null) {
            this.buyoutPrice = BuyoutPrice.from(buyoutPrice);
            isUpdated = true;
        }

        if(startTime != null) {
            this.startTime = startTime;
            isUpdated = true;
        }

        if(endTime != null) {
            this.endTime = endTime;
            isUpdated = true;
        }

        return isUpdated;
    }

    //스케줄러 경매전 -> 시작
    public void startAuction() {
        if(this.status != AuctionStatus.BEFORE_START) {
            throw new IllegalStateException("시작 대기 상태인 경매만 시작할 수 있습니다.");
        }
        this.status = AuctionStatus.ONGOING;
    }

    //스케줄러 경매시작 -> 종료
    public void endAuction() {
        if(this.status != AuctionStatus.ONGOING) {
            throw new IllegalStateException("진행 중 경매만 종료할 수 있습니다.");
        }
        this.status = AuctionStatus.ENDED;
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
        return Bid.of(this, bidder, bidAmount, BidType.NORMAL); //새 입찰 생성
    }

    //즉시구매
    public Bid buyOut(User buyer) {
        //검증로직
        if(this.status != AuctionStatus.ONGOING) {
            throw new IllegalStateException("진행 중인 경매가 아닙니다.");
        }
        if(LocalDateTime.now().isAfter(this.endTime)) {
            throw new IllegalStateException("경매가 종료되었습니다.");
        }
        if(this.product.getSeller().getId().equals(buyer.getId())) {
            throw new IllegalStateException("판매자는 즉시구매 할 수 없습니다.");
        }
        this.currentPrice = new CurrentPrice(this.buyoutPrice.getValue());  //현재가 -> 즉시구매가 갱신
        this.currentBidderId = buyer.getId();
        this.endTime= LocalDateTime.now();
        this.status = AuctionStatus.ENDED;  //경매 종료
        return Bid.of(this, buyer, new BidAmount(this.buyoutPrice.getValue()), BidType.BUYOUT);

    }

    public void delete() {
        this.status = AuctionStatus.CANCELLED;
    }
}
