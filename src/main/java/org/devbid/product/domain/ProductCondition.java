package org.devbid.product.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ProductCondition {
    PRISTINE,
    WORN,
    DAMAGED,
    BADLY_DAMAGED,
    RUINED
}
