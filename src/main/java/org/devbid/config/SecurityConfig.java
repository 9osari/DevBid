package org.devbid.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/users/register").permitAll()  // 회원가입만 허용
                .anyRequest().authenticated()  // 나머지는 인증 필요
            )
            .formLogin(form -> form
                .defaultSuccessUrl("/", true)  // 로그인 성공 시 홈으로
            );
        return http.build();
    }
}