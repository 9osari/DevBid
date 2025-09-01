package org.devbid.user.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Nickname {
    private String value;

    public Nickname(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("사용자 이름은 필수입니다.");
        }
        this.value = value;
    }
}
