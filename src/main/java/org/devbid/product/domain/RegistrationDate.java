package org.devbid.product.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class RegistrationDate {
    @Column(name = "registrationDate")
    private String value;
}
