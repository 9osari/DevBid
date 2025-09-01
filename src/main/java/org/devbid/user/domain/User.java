package org.devbid.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.devbid.user.application.command.BaseEntity;


@Entity
@Table(name = "USERS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "username", nullable = false, unique = true, length = 50))
    private Username username;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email", nullable = false, unique = true, length = 100))
    private Email email;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "password", nullable = false, length = 200))
    private Password password;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "nickname", nullable = false, unique = true, length = 50))
    private Nickname nickname;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "phone", nullable = false, length = 20))
    private Phone phone;

    public User(Username username, Email email, Password password, Nickname nickname, Phone phone) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.phone = phone;
    }

    public static User create(Username username, Email email, Password password, Nickname nickname, Phone phone) {
        return new User(username, email, password, nickname, phone);
    }

}
