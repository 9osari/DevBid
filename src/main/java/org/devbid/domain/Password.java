package org.devbid.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Objects;

@Getter
@Embeddable
public class Password {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Column(name = "password")
    private String encryptedValue;

    protected Password() {}

    //평문
    public Password(String plainPassword) {
        validatePassword(plainPassword);
        this.encryptedValue = encoder.encode(plainPassword);
    }

    //암호화
    private Password(String encryptedValue, boolean isEncrypted) {
            validateEncryptedPassword(encryptedValue);
            this.encryptedValue = encryptedValue;
    }


    private void validatePassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("암호화된 패스워드는 필수입니다.");
        }
        if(plainPassword.length() < 6 ||  plainPassword.length() > 20) {
            throw new IllegalArgumentException("암호는 6 ~ 20자");
        }
    }

    private void validateEncryptedPassword(String encryptedPassword) {
        if (encryptedPassword == null || encryptedPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("암호화는 필수입니다");
        }
        if(encryptedPassword.length() < 80) {
            throw new IllegalArgumentException("유효하지 않은 암호화된 패스워드");
        }
    }


    public boolean matches(String encryptedPassword) {
        return encoder.matches(encryptedPassword, this.encryptedValue);
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
