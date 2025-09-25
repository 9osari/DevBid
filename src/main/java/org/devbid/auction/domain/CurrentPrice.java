package org.devbid.auction.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Embeddable
public class CurrentPrice {
    @Column(name = "current_price", nullable = false)
    private BigDecimal value;

    protected CurrentPrice() {}

    public CurrentPrice(BigDecimal value) {
        validateCurrentPrice(value);
        this.value = value;
    }
    private void validateCurrentPrice(BigDecimal value) {
        if(value == null) {
            throw new IllegalArgumentException("CurrentPrice cannot be null");
        }
        if(value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("CurrentPrice must be greater than 0");
        }
    }


    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        CurrentPrice currentPrice = (CurrentPrice) object;
        return Objects.equals(value, currentPrice.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
