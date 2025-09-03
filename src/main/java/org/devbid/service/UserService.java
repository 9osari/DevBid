package org.devbid.service;

import org.springframework.stereotype.Service;

@Service
public interface UserService {
    void rigisterUser(UserRegisterRequest user);
}
