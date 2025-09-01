package org.devbid.user.ui;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.devbid.user.application.command.UserRegistrationCommand;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class UserRegistrationRequest {
    private String username;
    private String email;
    private String password;
    private String nickname;
    private String phone;

    public UserRegistrationCommand toCommand() {
        return new UserRegistrationCommand(username, email, password, nickname, phone);
    }
}
