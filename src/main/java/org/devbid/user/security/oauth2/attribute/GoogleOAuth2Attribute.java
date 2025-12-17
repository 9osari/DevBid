package org.devbid.user.security.oauth2.attribute;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class GoogleOAuth2Attribute implements OAuth2Attribute{
    private final Map<String, Object> attributes;

    @Override
    public String getProviderUserId() {
        return String.valueOf(attributes.get("sub"));
    }

    @Override
    public String getEmail() {
        Object email = attributes.get("email");
        if(email == null) {
            throw new IllegalArgumentException("email is null");
        }
        return (String) email;
    }

    @Override
    public String getNickname() {
        Object nickname = attributes.get("nickname");
        if(nickname == null) {
            throw new IllegalArgumentException("nickname is null");
        }
        return (String) nickname;
    }
}
