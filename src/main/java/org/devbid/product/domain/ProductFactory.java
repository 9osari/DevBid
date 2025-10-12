package org.devbid.product.domain;

import org.devbid.user.domain.User;

public class ProductFactory {
    public static Product createFromPrimitives(
            String productName,
            String description,
            Category category,
            String condition,
            User seller) {
        return Product.of(
                new ProductName(productName),
                new Description(description),
                category,
                ProductCondition.valueOf(condition.toUpperCase()),
                seller
        );
    }
}
