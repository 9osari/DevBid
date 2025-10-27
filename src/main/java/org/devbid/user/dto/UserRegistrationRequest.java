package org.devbid.user.dto;

public record UserRegistrationRequest(
        String username,
        String password,
        String email,
        String nickname,
        String phone,
        String zipCode,
        String street,
        String detail
) {

}

