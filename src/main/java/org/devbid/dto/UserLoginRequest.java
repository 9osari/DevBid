package org.devbid.dto;

public record UserLoginRequest(
   String username,
   String rawPassword
) {}
