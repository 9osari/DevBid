package org.devbid.security;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.devbid.domain.User;
import org.devbid.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionManager {

    private final UserRepository userRepository;

    public void createSession(User user, HttpSession session) {
        session.setAttribute("loginUserId", user.getId());    //id값
    }

    public void destroySession(HttpSession session) {
        session.invalidate();
    }

    public User getLoginUser(HttpSession session) {
        Long userid = (Long) session.getAttribute("loginUserId");
        if (userid == null) {
            return null;
        }
        return userRepository.findById(userid).orElse(null);
    }
}
