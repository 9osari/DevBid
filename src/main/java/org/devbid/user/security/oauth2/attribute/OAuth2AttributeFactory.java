package org.devbid.user.security.oauth2.attribute;

import java.util.Map;

public class OAuth2AttributeFactory {
    public static OAuth2Attribute of(String providerId, Map<String, Object> attributes) {
        return switch (providerId) {
            case "kakao" -> new KakaoOAuth2Attribute(attributes);
            case "google" -> new GoogleOAuth2Attribute(attributes);
            default -> throw new IllegalArgumentException("Unsupported OAuth2 provider: " + providerId);
        };
    }
}