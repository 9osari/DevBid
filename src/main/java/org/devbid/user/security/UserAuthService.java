package org.devbid.user.security;

import lombok.RequiredArgsConstructor;
import org.devbid.user.domain.User;
import org.devbid.user.domain.UserStatus;
import org.devbid.user.domain.Username;
import org.devbid.user.repository.UserRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAuthService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Username usernameVO = new Username(username);
        User user = userRepository.findByUsername(usernameVO)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        if(user.getStatus() == UserStatus.INACTIVE) {
            throw new DisabledException("탈퇴한 회원입니다.");
        }

        return new AuthUser(
                user.getId(),
                user.getUsername().getValue(),
                "{bcrypt}" + user.getPassword().getEncryptedValue(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
