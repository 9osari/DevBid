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

    public static Description from(String value) {
        return new Description(value);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Description description = (Description) object;
        return value.equals(description.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
