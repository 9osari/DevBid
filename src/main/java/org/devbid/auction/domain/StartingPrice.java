package org.devbid.auction.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Embeddable
public class StartingPrice {
    @Column(name = "starting_price", nullable = false)
    private BigDecimal value;

    public StartingPrice(BigDecimal value) {
        validateStartingPrice(value);
        this.value = value;
    }

    protected StartingPrice() {}

    public static StartingPrice from(BigDecimal value) {
        return new StartingPrice(value);
    }

    private void validateStartingPrice(BigDecimal value) {
        if(value == null) {
            throw new IllegalArgumentException("StartingPrice cannot be null");
        }
        if(value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("StartingPrice must be greater than 0");
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        StartingPrice startingPrice = (StartingPrice) object;
        return Objects.equals(value, startingPrice.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
