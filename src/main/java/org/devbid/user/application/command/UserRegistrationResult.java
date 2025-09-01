package org.devbid.user.application.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserRegistrationResult {
    private final Long id;
    private final String username;
    private final String email;

    public static UserRegistrationResult of(Long id, String username, String email) {
        return new UserRegistrationResult(id, username, email);
    }
}
