package org.devbid.product.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.Objects;

@Getter
@Embeddable
public class ProductName {
    @Column(name = "product_name", nullable = false)
    private String value;

    protected  ProductName() {}

    public ProductName(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        ProductName productName = (ProductName) object;
        return Objects.equals(value, productName.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
