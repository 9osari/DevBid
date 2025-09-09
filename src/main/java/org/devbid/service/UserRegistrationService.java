package org.devbid.service;

import org.devbid.dto.UserRegistrationRequest;

public interface UserRegistrationService {
    void registerUser(UserRegistrationRequest user);
}
