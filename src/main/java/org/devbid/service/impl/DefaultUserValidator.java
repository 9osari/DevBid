package org.devbid.service.impl;

import lombok.RequiredArgsConstructor;
import org.devbid.domain.Email;
import org.devbid.domain.User;
import org.devbid.domain.Username;
import org.devbid.repository.UserRepository;
import org.devbid.service.UserValidator;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DefaultUserValidator implements UserValidator {

    private final UserRepository userRepository;

    @Override
    public void RegisterValidate(String username, String email) {
        Username usernameVO = new Username(username);
        if(userRepository.existsByUsername(usernameVO)) {
            throw new IllegalArgumentException("check username");
        }

        Email emailVO = new Email(email);
        if(userRepository.existsByEmail(emailVO)) {
            throw new IllegalArgumentException("check email");
        }
    }

    @Override
    public void UpdateValidate(String currentUsername, String email, String nickname, String phone) {
        Email emailVO = new Email(email);
        Optional<User> existingUser = userRepository.findByEmail(emailVO);
        if(existingUser.isPresent() && !existingUser.get().getUsername().equals(currentUsername)) {
            throw new IllegalArgumentException("check email");
        }
    }
}
