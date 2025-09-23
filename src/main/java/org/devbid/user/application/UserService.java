package org.devbid.user.application;

import org.devbid.user.domain.UserEntity;
import org.devbid.user.domain.UserDto;
import org.devbid.user.dto.UserUpdateRequest;

import java.util.List;

public interface UserService {
    //저장
    void registerUser(UserDto userDto);

    //수정
    void updateUser(Long id, UserUpdateRequest request);

    //삭제
    void deleteUser(Long id);

    //조회용
    List<UserEntity> findAllUsers();
    UserEntity findById(Long id);
    UserEntity findByUsername(String username);
    long getUserCount();
}
