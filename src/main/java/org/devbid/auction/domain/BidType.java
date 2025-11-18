package org.devbid.auction.domain;

import lombok.Getter;

@Getter
public enum BidType {
    NORMAL("BID_PLACED"),
    BUYOUT("BUY_OUT");

    private final String eventType;

    BidType(String eventType) {
        this.eventType = eventType;
    }
}
