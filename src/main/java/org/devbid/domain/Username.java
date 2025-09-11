package org.devbid.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.Objects;

@Getter
@Embeddable
public class Username {
    @Column(name = "username")
    private String value;

    protected Username() {}

    public Username(String value) {
        validateUsername(value);
        this.value = value;
    }

    private void validateUsername(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (value.length() < 2 || value.length() > 20) {
            throw new IllegalArgumentException("Username length should be between 2 and 20 characters");
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Username username = (Username) object;
        return Objects.equals(value, username.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
