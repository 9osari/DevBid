package org.devbid.product.dto;

import java.math.BigDecimal;

public record ProductRegistrationRequest(
        String productName,
        String description,
        BigDecimal price,
        Long categoryId,
        String condition,
        Long sellerId
) {

}
