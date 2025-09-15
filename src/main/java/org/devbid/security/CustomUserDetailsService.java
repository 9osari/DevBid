package org.devbid.security;

import lombok.RequiredArgsConstructor;
import org.devbid.domain.User;
import org.devbid.domain.Username;
import org.devbid.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Username usernameVO = new Username(username);
        User user = userRepository.findByUsername(usernameVO)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername().getValue())
                .password("{bcrypt}" + user.getPassword().getEncryptedValue()) // {bcrypt} 접두사 추가
                .roles("USER")
                .build();
    }
}