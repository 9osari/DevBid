package org.devbid.user.ui;

import org.devbid.user.domain.*;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toEntity(UserRegistrationRequest request) {
        if (request == null) {
            return null;
        }

        return User.create(
            new Username(request.getUsername()),
            new Email(request.getEmail()),
            new Password(request.getPassword()),
            new Nickname(request.getNickname()),
            new Phone(request.getPhone())
        );
    }
}
