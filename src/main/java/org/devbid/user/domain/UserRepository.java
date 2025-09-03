package org.devbid.user.domain;

import org.devbid.user.domain.model.User;

import java.util.Optional;

//도메인에서 쓸 것들
public interface UserRepository {
    Optional<User> findByUsernameValue(String username);
    Optional<User> findByEmailValue(String email);
    User save(User user);
}
