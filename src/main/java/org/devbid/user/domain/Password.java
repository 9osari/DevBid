package org.devbid.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.Objects;

@Getter
@Embeddable
public class Password {
    @Column(name = "password")
    private String encryptedValue;

    protected Password() {}

    //암호화
    public Password(String encryptedValue) {
        validateEncryptedPassword(encryptedValue);
        this.encryptedValue = encryptedValue;
    }

    private void validateEncryptedPassword(String encryptedPassword) {
        if (encryptedPassword == null || encryptedPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("암호화는 필수입니다");
        }
        if(encryptedPassword.length() < 60) {
            throw new IllegalArgumentException("유효하지 않은 암호화된 패스워드");
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Password password = (Password) object;
        return Objects.equals(encryptedValue, password.encryptedValue);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(encryptedValue);
    }
}
