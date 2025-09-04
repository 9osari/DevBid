package org.devbid.service.impl;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.devbid.security.SessionManager;
import org.devbid.domain.User;
import org.devbid.dto.UserLoginRequest;
import org.devbid.repository.UserRepository;
import org.devbid.service.UserLoginService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserLoginServiceImpl implements UserLoginService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionManager sessionManager;


    @Override
    public void login(UserLoginRequest request, HttpSession session) {
        User user = userRepository.findByUsername(request.username())
                .orElse(null);

        if (user == null || !user.matchesPassword(request.rawPassword(), passwordEncoder)) {
            throw new IllegalArgumentException("check username and password");
        }

        System.out.println("login successfully create session");
        sessionManager.createSession(user, session);
        System.out.println("=== login complete ==");
    }
}
