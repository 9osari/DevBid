package org.devbid.service;

import org.devbid.dto.UserRegisterRequest;

public interface UserRegistrationService {
    void registerUser(UserRegisterRequest user);
}
