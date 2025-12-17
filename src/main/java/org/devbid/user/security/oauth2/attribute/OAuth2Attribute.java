package org.devbid.user.security.oauth2.attribute;

public interface OAuth2Attribute {
    String getProviderUserId();
    String getEmail();
    String getNickname();
}
