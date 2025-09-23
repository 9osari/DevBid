package org.devbid.user.application;

public interface UserValidator {
    void RegisterValidate(String username, String email);
    void UpdateValidate(String currentUsername, String email, String nickname, String phone);
}
