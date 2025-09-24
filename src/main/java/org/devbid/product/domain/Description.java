package org.devbid.product.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class Description {
    @Column(name = "description")
    private String value;

    protected  Description() {}

    public Description(String value) {
        this.value = value;
    }
}
