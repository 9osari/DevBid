package org.devbid.user.application;

import lombok.RequiredArgsConstructor;
import org.devbid.user.domain.Email;
import org.devbid.user.domain.User;
import org.devbid.user.domain.Username;
import org.devbid.user.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserValidatorImpl implements UserValidator {

    private final UserRepository userRepository;

    @Override
    public void validateForRegistration(String username, String email) {
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
    public void validateForUpdate(String currentUsername, String email, String nickname, String phone) {
        Email emailVO = new Email(email);
        Optional<User> existingUser = userRepository.findByEmail(emailVO);
        if(existingUser.isPresent() && !existingUser.get().getUsername().getValue().equals(currentUsername)) {
            throw new IllegalArgumentException("check email");
        }
    }
}
