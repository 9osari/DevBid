package org.devbid.application;

import org.devbid.domain.User;
import org.devbid.domain.UserDto;
import org.devbid.dto.UserUpdateRequest;

import java.util.List;

public interface UserService {
    //저장
    void registerUser(UserDto userDto);

    //수정
    void updateUser(String username, UserUpdateRequest request);

    //삭제
    void deleteUser(String username);

    //조회용
    List<User> findAllUsers();
    User findById(Long id);
    User findByUsername(String username);
    long getUserCount();
}
