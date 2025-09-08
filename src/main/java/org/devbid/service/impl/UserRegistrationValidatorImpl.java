package org.devbid.service.impl;

import lombok.RequiredArgsConstructor;
import org.devbid.repository.UserRepository;
import org.devbid.service.UserRegistrationValidator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRegistrationValidatorImpl implements UserRegistrationValidator {

    private final UserRepository userRepository;

    @Override
    public void validateForRegistration(String username, String email) {
        if(userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("check username");
        }

        if(userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("check email");
        }
    }
}
