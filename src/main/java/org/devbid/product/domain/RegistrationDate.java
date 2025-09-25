package org.devbid.product.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Embeddable
public class RegistrationDate {
    @Column(name = "registration_date", nullable = false)
    private LocalDateTime value;

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        RegistrationDate registrationDate = (RegistrationDate) object;
        return Objects.equals(value, registrationDate.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
