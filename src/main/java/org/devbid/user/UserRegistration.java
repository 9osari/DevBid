package org.devbid.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor    //생성자 자동생성
public class UserRegistration implements UserRegistrationService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;  

    @Override
    public void register(User user, String password) {
        userValidator.validateDuplicateUsername(user.getUsername());

        userValidator.validateDuplicateEmail(user.getEmail());

        userRepository.save(user);
    }
}
