package org.devbid.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.devbid.domain.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class RegisterServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RegisterValidator validator;
    private final UserRepository userRepository;

    @Override
    public void rigisterUser(UserRegisterRequest request) {
        validator.validate(request);

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = userMapper.toEntity(request, encodedPassword);

        userRepository.save(user);
    }
}
