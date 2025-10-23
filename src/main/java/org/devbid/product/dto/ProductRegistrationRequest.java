package org.devbid.product.dto;

import java.util.List;

public record ProductRegistrationRequest(
        String productName,
        String description,
        Long categoryId,
        String condition,
        Long sellerId,
        String mainImageKey,
        List<String> subImageKeys
) {}
