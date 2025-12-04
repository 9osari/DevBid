package org.devbid.user.repository;

import org.devbid.user.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(Username username);
    boolean existsByEmail(Email email);

    @Modifying
    @Query("UPDATE User u SET u.status = 'INACTIVE' WHERE u.username = :username")
    int deleteByUsername(@Param("username") Username username);

    Optional<User> findByUsername(Username username);
    Optional<User> findByEmail(Email email);

    long count();
    int countByStatus(UserStatus status);

    Optional<User> findBySocialAuthInfo_ProviderIdAndSocialAuthInfo_ProviderUserId(String providerId, String socialAuthInfoId);
}
