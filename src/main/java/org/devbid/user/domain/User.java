package org.devbid.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.devbid.common.BaseEntity;


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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status =  UserStatus.ACTIVE;

    public User(Username username, Email email, Password password, Nickname nickname, Phone phone) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.phone = phone;
    }

    public static User of(Username username, Email email, Password password, Nickname nickname, Phone phone) {
        return new User(username, email, password, nickname, phone);
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
