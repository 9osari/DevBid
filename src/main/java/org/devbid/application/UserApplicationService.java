package org.devbid.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.domain.User;
import org.devbid.domain.UserDto;
import org.devbid.domain.Username;
import org.devbid.dto.UserUpdateRequest;
import org.devbid.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserApplicationService implements UserService {
    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void registerUser(UserDto userDto) {
        User user = User.register(userDto, userValidator, passwordEncoder);
        userRepository.save(user);
    }

    @Override
    public void updateUser(String username, UserUpdateRequest request) {
        log.info("update user: {}", username);
        log.info("request: {}", request);

        userValidator.UpdateValidate(username, request.email(), request.nickname(), request.phone());

        Username usernameVO = new Username(username);
        User user = userRepository.findByUsername(usernameVO).orElseThrow(() -> new IllegalArgumentException("i can't find user: " + username));

        boolean updated = user.updateProfile(request.email(), request.nickname(), request.phone());

        if(!updated) {
            log.info("No change detected for user: {}", username);
        }
        log.info("User update successfully: {}", username);
    }

    @Override
    public void deleteUser(String username) {
        Username usernameVO = new Username(username);

        User user = userRepository.findByUsername(usernameVO)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        int deletedCount = userRepository.deleteByUsername(usernameVO);

        if (deletedCount == 0) {
            throw new RuntimeException("Failed to delete user: " + username);
        }
        log.info("User deleted successfully: {}", username);
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
        Username usernameVO = new Username(username);
        return userRepository.findByUsername(usernameVO).orElse(null);
    }

    @Override
    public long getUserCount() {
        return userRepository.count();
    }
}
