package org.devbid.user.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.infrastructure.config.PasswordEncoder;
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
                request.phone(),
                request.zipCode(),
                request.street(),
                request.detail()
        );
        userRepository.save(user);
    }

    @Override
    public void updateUser(Long id, UserUpdateRequest request) {
        log.info("update user: {}", id); // username → id
        log.info("request: {}", request);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User id not found: " + id));

        userValidator.validateForUpdate(
                user.getUsername().getValue(),
                request.email(),
                request.nickname(),
                request.phone(),
                request.zipCode(),
                request.street(),
                request.detail());

        boolean updated = user.updateProfile(
                request.email(),
                request.nickname(),
                request.phone(),
                request.zipCode(),
                request.street(),
                request.detail()
        );

        if(!updated) {
            log.info("No change detected for user: {}", id);
        }
        log.info("User update successfully: {}", id);
    }

    @Override
    public void deleteUser(Long id) {
        //JPA 더티 체킹(Dirty Checking)
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User id not found: " + id)); // 1. 영속성 컨텍스트에 저장

        user.softDelete();  // 2. 엔티티 상태 변경 (Dirty)

        log.info("User deleted successfully: {}", id);
        // 3. 메서드 종료 → @Transactional이 커밋
        // 4. JPA가 변경 감지 → 자동 UPDATE
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
