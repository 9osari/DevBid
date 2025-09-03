package org.devbid.user.application;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserRegistrationResult {
    //클라이언트에 반환할 정보들
    private final Long id;
    private final String username;
    private final String email;

    public static UserRegistrationResult of(Long id, String username, String email) {
        return new UserRegistrationResult(id, username, email);
    }
}
