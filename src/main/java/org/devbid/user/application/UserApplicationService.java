package org.devbid.user.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.config.PasswordEncoder;
import org.devbid.user.domain.User;
import org.devbid.user.domain.UserFactory;
import org.devbid.user.domain.Username;
import org.devbid.user.dto.UserRegistrationRequest;
import org.devbid.user.dto.UserUpdateRequest;
import org.devbid.user.repository.UserRepository;
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
    public void registerUser(UserRegistrationRequest request) {
        userValidator.validateForRegistration(request.username(), request.email());
        String encryptedPassword = passwordEncoder.encode(request.password());

        User user = UserFactory.createFromPrimitives(
                request.username(),
                request.email(),
                encryptedPassword,
                request.nickname(),
                request.phone()
        );
        userRepository.save(user);
    }

    @Override
    public void updateUser(Long id, UserUpdateRequest request) {
        log.info("update user: {}", id); // username → id
        log.info("request: {}", request);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User id not found: " + id));

        userValidator.validateForUpdate(user.getUserName().getValue(),
                request.email(),
                request.nickname(),
                request.phone());

        boolean updated = user.updateProfile(request.email(), request.nickname(), request.phone());

        if(!updated) {
            log.info("No change detected for user: {}", id);
        }
        log.info("User update successfully: {}", id);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User id not found: " + id));

        userRepository.deleteById(id);

        log.info("User deleted successfully: {}", id);
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
