package org.devbid.user.application.command;

import lombok.RequiredArgsConstructor;
import org.devbid.user.application.UserRegistrationResult;
import org.devbid.user.domain.model.Email;
import org.devbid.user.domain.model.Nickname;
import org.devbid.user.domain.model.Password;
import org.devbid.user.domain.model.Phone;
import org.devbid.user.domain.model.User;
import org.devbid.user.domain.UserDomainService;
import org.devbid.user.domain.UserRepository;
import org.devbid.user.domain.model.Username;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserRegistrationCommandHandler {
    private final UserDomainService userDomainService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserRegistrationResult registerUser(UserRegistrationCommand command) {
        String encodedPassword = passwordEncoder.encode(command.getPassword());

        Username username = new Username(command.getUsername());
        Email email = new Email(command.getEmail());
        Password password = new Password(encodedPassword);
        Nickname nickname = new Nickname(command.getNickname());
        Phone phone = new Phone(command.getPhone());

        userDomainService.checkDuplicate(username, email);

        User user = User.create(username, email, password, nickname, phone);

        User savedUser = userRepository.save(user);

        return UserRegistrationResult.of(savedUser.getId(), savedUser.getUsername().getValue(), savedUser.getEmail().getValue());
    }
}
