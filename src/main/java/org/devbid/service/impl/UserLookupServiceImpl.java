package org.devbid.service.impl;

import lombok.RequiredArgsConstructor;
import org.devbid.domain.User;
import org.devbid.repository.UserRepository;
import org.devbid.service.UserLookupService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserLookupServiceImpl implements UserLookupService {
    private final UserRepository userRepository;

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public long getUserCount() {
        return userRepository.count();
    }
}
