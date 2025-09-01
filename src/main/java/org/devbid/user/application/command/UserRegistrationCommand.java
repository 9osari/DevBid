package org.devbid.user.application.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserRegistrationCommand {
    private final String username;
    private final String email;
    private final String password;
    private final String nickname;
    private final String phone;
}
