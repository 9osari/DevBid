package org.devbid.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
//추후 분리예정
public class UserDto {
    private Long id;
    private String password;
    private String username;
    private String email;
    private String nickname;
    private String phone;
}
