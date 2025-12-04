package org.devbid.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class SocialAuthInfo {
    @Column(name = "provider")
    private String providerId;

    @Column(name = "provider_id")
    private String providerUserId;

    protected SocialAuthInfo() {}

    public SocialAuthInfo(String providerId, String providerUserId) {
        this.providerId = providerId;
        this.providerUserId = providerUserId;
    }
    
}
