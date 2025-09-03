package org.devbid.service;


import org.springframework.stereotype.Component;

@Component
public class RegisterValidator {
    public void validate(UserRegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("사용자명은 필수입니다.");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("이메일은 필수입니다.");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
        if (request.getNickname() == null || request.getNickname().trim().isEmpty()) {
            throw new IllegalArgumentException("닉네임은 필수입니다.");
        }
        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("전화번호는 필수입니다.");
        }
    }

}
