package org.devbid.user.ui;

import org.devbid.user.domain.Email;
import org.devbid.user.domain.Nickname;
import org.devbid.user.domain.Password;
import org.devbid.user.domain.Phone;
import org.devbid.user.domain.User;
import org.devbid.user.domain.Username;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toEntity(UserRegistrationRequest dto) {
        if(dto != null) {
            return User.create(
                    new Username(dto.getUsername()),
                    new Email(dto.getEmail()),
                    new Password(dto.getPassword()),
                    new Nickname(dto.getNickname()),
                    new Phone(dto.getPhone())
            );
        } return null;
    }
}
