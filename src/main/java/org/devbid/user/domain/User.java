package org.devbid.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.devbid.infrastructure.common.BaseEntity;
import org.hibernate.annotations.DynamicUpdate;


@Entity
@DynamicUpdate
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
    private SocialAuthInfo socialAuthInfo;

    @Embedded
    private Email email;

    @Embedded
    private Password password;

    @Embedded
    private Nickname nickname;

    @Embedded
    private Phone phone;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status =  UserStatus.ACTIVE;

    public User(Username username,
                Email email,
                Password password,
                Nickname nickname,
                Phone phone,
                Address address) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.phone = phone;
        this.address = address;
    }

    public static User create(Username username,
                              Email email,
                              Password password,
                              Nickname nickname,
                              Phone phone,
                              Address address) {
        return new User(
                username,
                email,
                password,
                nickname,
                phone,
                address);
    }

    //소셜 회원가입
    public static User createFromSocialAuth(SocialAuthInfo socialAuthInfo,
                                            Email email,
                                            Nickname nickname
    ) {
        User user = new User();
        user.socialAuthInfo = socialAuthInfo;
        user.email = email;
        user.nickname = nickname;
        user.password = null;
        user.phone = null;
        user.address = null;
        user.username = null;
        user.status = UserStatus.ACTIVE;

        String generatedUsername = socialAuthInfo.getProviderId() + "-" + socialAuthInfo.getProviderUserId();
        user.username = Username.from(generatedUsername);

        return user;
    }

    public boolean updateProfile(String email, String nickname, String phone,  String zipCode, String street, String detail) {
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

        Address newAddress = new Address(zipCode, street, detail);
        if(!this.address.equals(newAddress)) {
            this.address = newAddress;
            isUpdated = true;
        }

        return isUpdated;
    }

    public void changePassword(String plainPassword) {
        this.password = new Password(plainPassword);
    }

    public void softDelete() {
        this.status = UserStatus.INACTIVE;
    }
}
