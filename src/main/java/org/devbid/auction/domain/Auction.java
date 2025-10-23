package org.devbid.auction.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.devbid.infrastructure.common.BaseEntity;
import org.devbid.product.domain.Product;
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
}
