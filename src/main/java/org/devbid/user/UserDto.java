package org.devbid.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor    //생성자 자동생성
public class UserDto {
    private final Long id;
    private final String username;
    private final String email;
    private final String nickname;
    private final String phone;

    public static UserDto of(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getNickname(), user.getPhone());
    }
}
