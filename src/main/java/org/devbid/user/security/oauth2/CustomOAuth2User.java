package org.devbid.user.security.oauth2;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.devbid.user.domain.User;
import org.devbid.user.domain.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User, UserDetails, Serializable {
    @Getter
    private final User user;
    private transient final Map<String, Object> attributes; //transient => 이 필드는 Redis에 저장X
    private static final long serialVersionUID = 1L;    //redis 직렬화

    @Override
    public String getPassword() {
        if(user.getPassword() == null) {
            return "";
        }
        return "{bcrypt}" + user.getPassword().getEncryptedValue(); //prefix 필수
    }

    @Override
    public String getUsername() {
        return user.getUsername() != null
            ? user.getUsername().getValue()
            : user.getEmail().getValue(); // prefix 필수
    }

    @Override
    public String getName() {
        return user.getUsername() != null
            ? user.getUsername().getValue()
            : user.getNickname().getValue();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() == UserStatus.ACTIVE; //사용자 상태 확인
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")); //권한 설정
    }
}
