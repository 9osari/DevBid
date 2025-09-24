package org.devbid.product.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class ProductName {
    @Column(name = "productName")
    private String value;

    protected  ProductName() {}

    public ProductName(String value) {
        this.value = value;
    }
}
