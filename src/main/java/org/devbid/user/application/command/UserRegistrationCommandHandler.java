package org.devbid.user.application.command;

import lombok.RequiredArgsConstructor;
import org.devbid.user.domain.Email;
import org.devbid.user.domain.Nickname;
import org.devbid.user.domain.Password;
import org.devbid.user.domain.Phone;
import org.devbid.user.domain.User;
import org.devbid.user.domain.UserDomainService;
import org.devbid.user.domain.UserRepository;
import org.devbid.user.domain.Username;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserRegistrationCommandHandler {
    private final UserDomainService userDomainService;
    private final UserRepository userRepository;

    public UserRegistrationResult handle(UserRegistrationCommand command) {
        Username username = new Username(command.getUsername());
        Email email = new Email(command.getEmail());
        Password password = new Password(command.getPassword());
        Nickname nickname = new Nickname(command.getNickname());
        Phone phone = new Phone(command.getPhone());

        userDomainService.checkDuplicate(username, email);

        User user = User.create(username, email, password, nickname, phone);

        User savedUser = userRepository.save(user);

        return UserRegistrationResult.of(savedUser.getId(), savedUser.getUsername().getValue(), savedUser.getEmail().getValue());
    }
}
