package org.devbid.product.domain;

import org.devbid.user.domain.User;

import java.math.BigDecimal;

public class ProductFactory {
    public static Product createFromPrimitives(
            String productName,
            String description,
            BigDecimal price,
            Category category,
            String condition,
            User seller) {
        return Product.of(
                new ProductName(productName),
                new Description(description),
                new Price(price),
                category,
                ProductCondition.valueOf(condition.toUpperCase()),
                seller
        );
    }
}
