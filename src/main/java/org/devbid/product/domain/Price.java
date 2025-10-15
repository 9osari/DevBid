package org.devbid.product.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Embeddable
public class Price {
    @Column(name = "price" , nullable = false)
    private BigDecimal value;

    protected  Price() {}

    public Price(BigDecimal value) {
        validatePrice(value);
        this.value = value;
    }

    private void validatePrice(BigDecimal value) {
        if(Objects.isNull(value)) {
            throw new IllegalArgumentException("가격은 필수입니다.");
        }

        if(value.compareTo(BigDecimal.valueOf(1000)) < 0) {
            throw new IllegalArgumentException("가격은 1000원 이상이어야 합니다.");
        }

        if(value.compareTo(BigDecimal.valueOf(1_000_000_000)) > 0) {
            throw new IllegalArgumentException("가격은 10억을 초과할 수 없습니다.");
        }
    }

    @Override
    public boolean equals(Object object) {
        if(this == object) return true; // 성능 최적화
        if (object == null || getClass() != object.getClass()) return false;
        Price price = (Price) object;
        return Objects.equals(value, price.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getValue());
    }
}
