package org.devbid.user.domain.model;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA용
public class Email{
    private String value;

    public Email(String value) {
        if (!value.contains("@")) {
            throw new IllegalArgumentException("이메일 형식 아님");
        }
        this.value = value;
    }
}
