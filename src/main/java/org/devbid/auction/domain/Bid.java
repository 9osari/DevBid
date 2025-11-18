package org.devbid.auction.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.devbid.infrastructure.common.BaseEntity;
import org.devbid.user.domain.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "bids")
public class Bid extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bidder_id", nullable = false)
    private User bidder;

    @Embedded
    private BidAmount bidAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "bid_type", nullable = false)
    private BidType type = BidType.NORMAL;

    public static Bid of(Auction auction, User bidder, BidAmount bidAmount, BidType bidtype) {
        Bid bid = new Bid();
        bid.auction = auction;
        bid.bidder = bidder;
        bid.bidAmount = bidAmount;
        bid.type = bidtype;
        return bid;
    }
}
