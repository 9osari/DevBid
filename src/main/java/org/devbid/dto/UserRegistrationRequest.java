package org.devbid.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.devbid.domain.*;

public record UserRegistrationRequest(
        @NotBlank(message = "Username is required.")
        @Size(min = 2, max = 20, message = "Username must be between 2 and 20 characters.")
        String username,

        @NotBlank(message = "Password is required.")
        @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters.")
        String password,

        @NotBlank(message = "Email is required.")
        @Email(message = "Invalid email format.")
        @Size(max = 100, message = "Email cannot exceed 100 characters.")
        String email,

        @NotBlank(message = "Nickname is required.")
        @Size(min = 2, max = 50, message = "Nickname must be between 2 and 50 characters.")
        String nickname,

        @NotBlank(message = "Phone number is required.")
        @Pattern(regexp = "^[0-9-]+$", message = "Phone number can only contain numbers and hyphens.")
        @Size(max = 20, message = "Phone number cannot exceed 20 characters.")
        String phone
) {
        public UserDto toDto() {
                return UserDto.builder()
                        .username(this.username)
                        .email(this.email)
                        .password(this.password)
                        .nickname(this.nickname)
                        .phone(this.phone)
                        .build();
        }
}

