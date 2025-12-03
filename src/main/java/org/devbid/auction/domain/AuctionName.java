package org.devbid.auction.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.Objects;

@Getter
@Embeddable
public class AuctionName {
    @Column(name = "auction_name", nullable = false)
    private String value;

    public AuctionName(String value) {
        validatProductName(value);
        this.value = value;
    }

    protected AuctionName() {}

    private void validatProductName(String value) {
        if(value == null) {
            throw new IllegalArgumentException("Product Name cannot be null");
        }
        if(value.isBlank()) {
            throw new IllegalArgumentException("Product Name cannot be blank");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AuctionName auctionName = (AuctionName) o;
        return Objects.equals(value, auctionName.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getValue());
    }
}
