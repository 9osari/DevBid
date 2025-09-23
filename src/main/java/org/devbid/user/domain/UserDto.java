package org.devbid.user.domain;

import lombok.Builder;
import lombok.Getter;
import org.devbid.common.BaseEntity;

@Getter
@Builder
public class UserDto extends BaseEntity {
    private String username;
    private String email;
    private String password;
    private String nickname;
    private String phone;


}
