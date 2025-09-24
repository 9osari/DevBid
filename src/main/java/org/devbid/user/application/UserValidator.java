package org.devbid.user.application;

public interface UserValidator {
    void validateForRegistration(String username, String email);
    void validateForUpdate(String currentUsername, String email, String nickname, String phone);
}
