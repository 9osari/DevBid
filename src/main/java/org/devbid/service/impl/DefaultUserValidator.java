package org.devbid.service.impl;

import lombok.RequiredArgsConstructor;
import org.devbid.repository.UserRepository;
import org.devbid.service.UserValidator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultUserValidator implements UserValidator {

    private final UserRepository userRepository;

    @Override
    public void RegisterValidate(String username, String email) {
        if(userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("check username");
        }

        if(userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("check email");
        }
    }

    @Override
    public void UpdateValidate(String email, String nickname, String phone) {

    }
}
