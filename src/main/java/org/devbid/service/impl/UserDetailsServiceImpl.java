package org.devbid.service.impl;

import lombok.RequiredArgsConstructor;
import org.devbid.domain.User;
import org.devbid.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("not found"));
        return org.springframework.security.core.userdetails.User
                        .withUsername(user.getUsername())
                        .password(user.getEncryptedPassword())
                        .roles("USER") // 로그인만 필요하니 USER 권한 하나만
                        .build();
    }
}
