package org.devbid.user.ui;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.devbid.user.application.UserRegistrationResult;

@AllArgsConstructor
@Getter
public class UserRegistrationResponse {
    private Long id;
    private String username;
    private String email;

    // Result → Response 변환 메서드
    public static UserRegistrationResponse from(UserRegistrationResult result) {
        return new UserRegistrationResponse(
            result.getId(),
            result.getUsername(),
            result.getEmail()
        );
    }
}
