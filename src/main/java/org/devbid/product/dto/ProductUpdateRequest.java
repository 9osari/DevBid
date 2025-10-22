package org.devbid.product.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductUpdateRequest(
        String productName,
        String description,
        BigDecimal price,
        Long categoryId,
        String condition,
        Long sellerId,

        //새로 업로드하는 이미지
        String mainImageKey,
        List<String> subImageKeys,

        //유지할 이미지 (key로 기존 이미지 식별 불가 - DB에 없기때문에)
        Long keepMainImageId,
        List<Long> keepSubImageIds
) {}
