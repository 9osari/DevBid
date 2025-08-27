package org.devbid.user;

import org.springframework.data.jpa.repository.JpaRepository;   //JPA 기반으로 CRUD(생성, 조회, 수정, 삭제) 기능을 미리 만들어둔 인터페이스

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}
