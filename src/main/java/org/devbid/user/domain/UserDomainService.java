package org.devbid.user.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDomainService {
    private final UserRepository userRepository;

    public void checkDuplicate(Username username, Email email) {
        if(userRepository.findByUsernameValue(username.getValue()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자명");
        }

        if(userRepository.findByEmailValue(email.getValue()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일");
        }
    }
}
