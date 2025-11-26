package org.devbid.auction.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Embeddable
public class BuyoutPrice {
    @Column(name = "buyout_price", nullable = false)
    private BigDecimal value;

    protected BuyoutPrice() {}

    public BuyoutPrice(BigDecimal value) {
        validateBuyoutPrice(value);
        this.value = value;
    }

    public static BuyoutPrice from(BigDecimal value) {
        return new BuyoutPrice(value);
    }

    private void validateBuyoutPrice(BigDecimal value) {
        if(value == null) {
            throw new IllegalArgumentException("BuyoutPrice cannot be null");
        }
        if(value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("BuyoutPrice must be greater than 0");
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        BuyoutPrice buyoutPrice = (BuyoutPrice) object;
        return Objects.equals(value, buyoutPrice.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
