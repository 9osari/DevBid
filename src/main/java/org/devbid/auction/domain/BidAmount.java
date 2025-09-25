package org.devbid.auction.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Embeddable
public class BidAmount {
    @Column(name = "bid_amount", nullable = false)
    private BigDecimal value;

    protected BidAmount() {}
    public BidAmount(BigDecimal value) {
        validateBidAmount(value);
        this.value = value;
    }

    private void validateBidAmount(BigDecimal value) {
        if(value == null) {
            throw new IllegalArgumentException("Bid Amount cannot be null");
        }
        if(value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Bid Amount must be greater than 0");
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        BidAmount bidAmount = (BidAmount) object;
        return Objects.equals(value, bidAmount.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

}
