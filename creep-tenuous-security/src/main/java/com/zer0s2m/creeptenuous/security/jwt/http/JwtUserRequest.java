package com.zer0s2m.creeptenuous.security.jwt.http;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record JwtUserRequest(
        @NotNull(message = "Please provide user login (Not NULL)")
        @NotBlank(message = "Please provide user login")
        String login,

        @NotNull(message = "Please provide password (Not NULL)")
        @NotBlank(message = "Please provide password")
        String password
) { }
