package org.devbid.service;

public interface UserValidator {
    void RegisterValidate(String username, String email);
    void UpdateValidate(String email, String nickname, String phone);
}
