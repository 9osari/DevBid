package org.devbid.user.security.oauth2.attribute;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class KakaoOAuth2Attribute implements OAuth2Attribute{
    private final Map<String, Object> attributes;

    @Override
    public String getProviderUserId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getEmail() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        if(kakaoAccount == null) {
            return "";
        }

        return (String) kakaoAccount.get("email");
    }

    @Override
    public String getNickname() {
        Map<String, Object> profile = (Map<String, Object>) attributes.get("properties");

        if(profile == null) {
            return "";
        }

        return (String) profile.get("nickname");
    }
}
