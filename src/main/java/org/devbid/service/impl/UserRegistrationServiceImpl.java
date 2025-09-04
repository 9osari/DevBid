package org.devbid.service.impl;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.devbid.domain.User;
import org.devbid.repository.UserRepository;
import org.devbid.dto.UserRegisterRequest;
import org.devbid.service.UserRegistrationService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserRegistrationServiceImpl implements UserRegistrationService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public void registerUser(UserRegisterRequest request) {
        assertNotDuplicated(request);

        String encoded = passwordEncoder.encode(request.password());
        userRepository.save(User.register(request, encoded));
    }

    private void assertNotDuplicated(UserRegisterRequest request) {
        if(userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("check username");
        }

        if(userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("check email");
        }
    }
}
