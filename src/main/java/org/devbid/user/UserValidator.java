package org.devbid.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component  //new UserValidator() 해서 직접 객체를 만드는 대신 스프링이 알아서 객체를 만들어줌.
@RequiredArgsConstructor    //생성자 자동생성
public class UserValidator {
    private final UserRepository userRepository;

    public void validateDuplicateUsername(String username) {
        if(userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 ID");
        }
    }

    public void validateDuplicateEmail(String email) {
        if(userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 EMAIL");
        }
    }
}
