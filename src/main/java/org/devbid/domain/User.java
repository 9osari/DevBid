package org.devbid.domain;

import jakarta.persistence.*;
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

    @Embedded
    private Username username;

    @Embedded
    private Email email;

    @Embedded
    private Password password;

    @Embedded
    private Nickname nickname;

    @Embedded
    private Phone phone;

    public static User register(String username, String email, String plainPassword, String nickname, String phone) {
        Username userNameVo = new Username(username);
        Email emailVo = new Email(email);
        Password passwordVo = new Password(plainPassword);
        Nickname nicknameVo = new Nickname(nickname);
        Phone phoneVo = new Phone(phone);

        return new User(userNameVo, emailVo, passwordVo, nicknameVo, phoneVo);
    }

    public User(Username userNameVo, Email emailVo, Password passwordVo, Nickname nicknameVo, Phone phoneVo) {
        this.username = userNameVo;
        this.email = emailVo;
        this.password = passwordVo;
        this.nickname = nicknameVo;
        this.phone = phoneVo;
    }


    public boolean updateProfile(String email, String nickname, String phone) {
        boolean isUpdated = false;

        Email newEmail = new Email(email);
        if(!this.email.equals(newEmail)) {
            this.email = newEmail;
            isUpdated = true;
        }

        Nickname newNickname = new Nickname(nickname);
        if(!this.nickname.equals(newNickname)) {
            this.nickname = newNickname;
            isUpdated = true;
        }

        Phone newPhone = new Phone(phone);
        if(!this.phone.equals(newPhone)) {
            this.phone = newPhone;
            isUpdated = true;
        }
        return isUpdated;
    }

    public void changePassword(String plainPassword) {
        this.password = new Password(plainPassword);
    }

}
