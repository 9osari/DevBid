package org.devbid.service;

import org.devbid.domain.User;

import java.util.List;

public interface UserLookupService {
    List<User> findAllUsers();
    User findById(Long id);
    long getUserCount();
}
