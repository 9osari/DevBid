package org.devbid.service;

import org.devbid.domain.User;
import org.devbid.dto.UserRegistrationRequest;
import org.devbid.dto.UserUpdateRequest;

import java.util.List;

public interface UserService {
    //저장
    void registerUser(UserRegistrationRequest user);

    //수정
    void updateUser(String username, UserUpdateRequest request);

    //조회용
    List<User> findAllUsers();
    User findById(Long id);
    User findByUsername(String username);
    long getUserCount();
}
