package org.devbid.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Embeddable
public class Address implements Serializable {
    @Column(name = "zip_code", length = 10)
    private String zipCode;

    @Column(name = "street")
    private String street;

    @Column(name = "detail")
    private String detail;

    protected Address() {}

    public Address(String zipCode, String street, String detail) {
        validateAddress(zipCode, street, detail);
        this.zipCode = zipCode;
        this.street = street;
        this.detail = detail;
    }

    private void validateAddress(String zipCode, String street, String detail) {
        if (zipCode == null || zipCode.isBlank() ||
            street == null || street.isBlank() ||
            detail == null || detail.isBlank()) {
            throw new IllegalArgumentException("주소는 필수입니다.");
        }

        if (!zipCode.matches("\\d{5}")) {
            throw new IllegalArgumentException("우편번호는 5자리 숫자여야 합니다.");
        }

        if (street.length() > 255) {
            throw new IllegalArgumentException("도로명 주소는 255자를 초과할 수 없습니다.");
        }
        if (detail.length() > 255) {
            throw new IllegalArgumentException("상세 주소는 255자를 초과할 수 없습니다.");
        }
    }

    @Override
    public boolean equals(Object object) {
        if(this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Address address = (Address) object;
        return Objects.equals(zipCode, address.zipCode) &&
                Objects.equals(street, address.street) &&
                Objects.equals(detail, address.detail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(zipCode, street, detail);
    }
}
