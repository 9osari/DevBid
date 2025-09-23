package org.devbid.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.devbid.application.PasswordEncoder;
import org.devbid.application.UserValidator;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity extends BaseEntity {
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

    public UserEntity(Username userNameVo, Email emailVo, Password passwordVo, Nickname nicknameVo, Phone phoneVo) {
        this.username = userNameVo;
        this.email = emailVo;
        this.password = passwordVo;
        this.nickname = nicknameVo;
        this.phone = phoneVo;
    }

    public static UserEntity register(UserDto dto, UserValidator validator, PasswordEncoder passwordEncoder) {
        validator.RegisterValidate(dto.getUsername(), dto.getEmail());

        Username username = new Username(dto.getUsername());
        Email email = new Email(dto.getEmail());
        String encodedPassword = passwordEncoder.encode(dto.getPassword()); // 암호화
        Password password = new Password(encodedPassword); // 암호화된 값으로 생성
        Nickname nickname = new Nickname(dto.getNickname());
        Phone phone = new Phone(dto.getPhone());

        return new UserEntity(username, email, password, nickname, phone);
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
