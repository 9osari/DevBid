package org.devbid.product.dto;

public record ProductRegistrationRequest(
        String productName,
        String description,
        Long categoryId,
        String condition,
        Long sellerId
) {

}
