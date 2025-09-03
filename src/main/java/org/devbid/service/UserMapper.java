package org.devbid.service;

import org.devbid.domain.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(UserRegisterRequest request, String encodedPassword) {
        return new User(request.getUsername(), request.getEmail(), encodedPassword, request.getNickname(), request.getPhone());
    }
}
