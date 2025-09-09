package org.devbid.service.impl;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.devbid.domain.User;
import org.devbid.repository.UserRepository;
import org.devbid.dto.UserRegistrationRequest;
import org.devbid.service.UserRegistrationService;
import org.devbid.service.UserRegistrationValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserRegistrationServiceImpl implements UserRegistrationService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserRegistrationValidator userRegistrationValidator;

    @Override
    public void registerUser(UserRegistrationRequest request) {
        userRegistrationValidator.validate(request.username(), request.email());

        String encoded = passwordEncoder.encode(request.password());

        User user = User.register(request.username(), request.email(), encoded, request.nickname(), request.phone());

        userRepository.save(user);
    }
}
