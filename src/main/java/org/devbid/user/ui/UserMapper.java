package org.devbid.user.ui;

import org.devbid.user.domain.User;

public interface UserMapper {
    User toEntity(UserRegistrationRequest request);
}
