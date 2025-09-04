package org.devbid.service;

import jakarta.servlet.http.HttpSession;
import org.devbid.dto.UserLoginRequest;

public interface UserLoginService {
    void login(UserLoginRequest request, HttpSession session);
}
