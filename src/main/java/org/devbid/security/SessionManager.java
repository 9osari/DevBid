package org.devbid.security;

import jakarta.servlet.http.HttpSession;
import org.devbid.domain.User;
import org.springframework.stereotype.Component;

@Component
public class SessionManager {
    public void createSession(User user, HttpSession session) {
        session.setAttribute("loginUser", user.getId());
    }

    public void destroySession(HttpSession session) {
        session.invalidate();
    }

    public Long getLoginUser(HttpSession session) {
        return (Long) session.getAttribute("loginUser");
    }
}
