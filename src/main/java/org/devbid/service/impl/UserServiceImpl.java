package org.devbid.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.domain.User;
import org.devbid.dto.UserRegistrationRequest;
import org.devbid.dto.UserUpdateRequest;
import org.devbid.repository.UserRepository;
import org.devbid.service.UserService;
import org.devbid.service.UserValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserValidator userRegistrationValidator;

    @Override
    public void registerUser(UserRegistrationRequest request) {
        userRegistrationValidator.RegisterValidate(request.username(), request.email());

        String encoded = passwordEncoder.encode(request.password());

        User user = User.register(request.username(), request.email(), encoded, request.nickname(), request.phone());

        userRepository.save(user);
    }

    @Override
    public void updateUser(String username, UserUpdateRequest request) {
        log.info("update user: {}", username);
        log.info("request: {}", request);

        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("i can't find user: " + username));

        boolean updated = user.updateProfile(request.email(), request.nickname(), request.phone());

        if(!updated) {
            log.info("User update successfully: {}", username);
        }
        log.info("No change detected for user: {}", username);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public long getUserCount() {
        return userRepository.count();
    }
}
