package org.devbid.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

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

