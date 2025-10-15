package org.devbid.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @NotBlank(message = "Email is required.")
        @Email(message = "Invalid email format.")
        @Size(max = 50, message = "Email cannot exceed 50 characters.")
        String email,

        @NotBlank(message = "Nickname is required.")
        @Size(min = 2, max = 50, message = "Nickname must be between 2 and 50 characters.")
        String nickname,

        @NotBlank(message = "Phone number is required.")
        @Pattern(regexp = "^[0-9-]+$", message = "Phone number can only contain numbers and hyphens.")
        @Size(max = 20, message = "Phone number cannot exceed 20 characters.")
        String phone,

        String zipCode,
        String street,
        String detail
) {
}