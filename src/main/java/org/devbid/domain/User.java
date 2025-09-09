package org.devbid.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.devbid.domain.common.BaseEntity;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Getter(AccessLevel.NONE)   //비밀번호 숨김
    @Column(nullable = false, length = 200)
    private String password;

    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    @Column(nullable = false, length = 20)
    private String phone;

    public User(String username, String email, String encryptedPassword, String nickname, String phone) {
        validateEncryptedPassword(encryptedPassword);

        this.username = username;
        this.email = email;
        this.password = encryptedPassword;
        this.nickname = nickname;
        this.phone = phone;
    }

    public String getEncryptedPassword() {
        return this.password;
    }

    public static User register(String username, String email, String encryptedPassword, String nickname, String phone) {
        return new User(
            username,
            email,
            encryptedPassword,
            nickname,
            phone
        );
    }

    public void updateProfile(String nickname, String phone) {
        if(isBlank(nickname)) throw new IllegalArgumentException("check nickname");
        if(isBlank(phone)) throw new IllegalArgumentException("check phone");

        this.nickname = nickname;
        this.phone = phone;
    }

    public void changePassword(String encryptedPassword) {
        validateEncryptedPassword(encryptedPassword);
        this.password = encryptedPassword;
    }

    private void validateEncryptedPassword(String encryptedPassword) {
        if (encryptedPassword == null || encryptedPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("암호화된 패스워드는 필수입니다.");
        }
        if (encryptedPassword.length() < 60) {
            throw new IllegalArgumentException("유효하지 않은 암호화된 패스워드입니다.");
        }
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

}
