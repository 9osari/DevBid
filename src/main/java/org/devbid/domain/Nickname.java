package org.devbid.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.Objects;

@Getter
@Embeddable
public class Nickname {
    @Column(name = "nickname")
    private String value;

    protected Nickname() {}

    public Nickname(String value) {
        validateNickname(value);
        this.value = value;
    }

    private void validateNickname(String value) {
        if (Objects.isNull(value) || value.trim().isEmpty()) {
            throw new IllegalArgumentException("닉네임은 필수입니다.");
        }

        if (value.length() < 2 || value.length() > 10) {
            throw new IllegalArgumentException("닉네임은 2~10자여야 합니다.");
        }
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Nickname username = (Nickname) object;
        return Objects.equals(value, username.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
