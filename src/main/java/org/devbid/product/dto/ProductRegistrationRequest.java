package org.devbid.product.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductRegistrationRequest(
        String productName,
        String description,
        BigDecimal price,
        Long categoryId,
        String condition,
        Long sellerId,
        String mainImageKey,
        List<String> subImageKeys
) {}
