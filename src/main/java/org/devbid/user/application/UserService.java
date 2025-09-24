package org.devbid.user.application;

import org.devbid.user.domain.User;
import org.devbid.user.dto.UserRegistrationRequest;
import org.devbid.user.dto.UserUpdateRequest;

import java.util.List;

public interface UserService {
    //저장
    void registerUser(UserRegistrationRequest request);

    //수정
    void updateUser(Long id, UserUpdateRequest request);

    //삭제
    void deleteUser(Long id);

    //조회용
    List<User> findAllUsers();
    User findById(Long id);
    User findByUsername(String username);
    long getUserCount();
}
