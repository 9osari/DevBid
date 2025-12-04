package org.devbid.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

@Getter
@Embeddable
public class Phone implements Serializable {
    private static final String PHONE_PATTERN = "^01[0-9]-[0-9]{4}-[0-9]{4}$";
    private static final Pattern PATTERN = Pattern.compile(PHONE_PATTERN);

    @Column(name = "phone")
    private String value;

    protected Phone() {}

    public Phone(String value) {
        validatePhone(value);
        this.value = value;
    }

    private void validatePhone(String value) {
        if(Objects.isNull(value) || value.trim().isEmpty()) {
            throw new IllegalArgumentException("전화번호는 필수 입니다");
        }
        if(!value.matches(PHONE_PATTERN)) {
            throw new IllegalArgumentException("전화번호는 숫자와 '-' 만 가능합니다");
        }
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Phone username = (Phone) object;
        return Objects.equals(value, username.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
