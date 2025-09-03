package org.devbid.user.application.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserRegistrationCommand {
    //사용자가 입력하는 정보
    private final String username;
    private final String email;
    private final String password;
    private final String nickname;
    private final String phone;
}
