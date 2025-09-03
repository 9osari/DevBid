package org.devbid.user.infrastructure;

import org.devbid.user.domain.UserRepository;
import org.devbid.user.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
//JpaRepository<User, Long> 를 상속받아 Spring이 런타임에 구현체 자동으로 생성
public interface JpaUserRepository extends UserRepository, JpaRepository<User, Long> {
    @Override
    Optional<User> findByUsernameValue(String username);

    @Override
    Optional<User> findByEmailValue(String email);
}
