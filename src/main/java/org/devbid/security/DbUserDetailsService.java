package org.devbid.security;

import lombok.RequiredArgsConstructor;
import org.devbid.domain.UserEntity;
import org.devbid.domain.UserStatus;
import org.devbid.domain.Username;
import org.devbid.repository.UserRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DbUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Username usernameVO = new Username(username);
        UserEntity userEntity = userRepository.findByUsername(usernameVO)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        if(userEntity.getStatus() == UserStatus.INACTIVE) {
            throw new DisabledException("탈퇴한 회원입니다.");
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(userEntity.getUsername().getValue())
                .password("{bcrypt}" + userEntity.getPassword().getEncryptedValue()) // {bcrypt} 접두사 추가
                .roles("USER")
                .build();
    }
}