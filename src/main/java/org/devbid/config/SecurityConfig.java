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

            //URL별 접근 권한을 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/users/register", "/users/registerPage").permitAll() //누구나 접근 가능
                .anyRequest().authenticated() //위에서 허용하지 않은 나머지 모든 요청은 인증(로그인) 필요
            ).formLogin(form -> form.permitAll());
        return http.build();
    }
}