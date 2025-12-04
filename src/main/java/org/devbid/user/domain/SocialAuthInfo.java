package org.devbid.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Embeddable
public class SocialAuthInfo implements Serializable {
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
