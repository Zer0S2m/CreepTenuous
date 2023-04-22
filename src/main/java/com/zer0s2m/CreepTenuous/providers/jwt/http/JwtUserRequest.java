package com.zer0s2m.CreepTenuous.providers.jwt.http;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record JwtUserRequest(
        @NotNull(message = "Please provide password (Not NULL)")
        @NotBlank(message = "Please provide password")
        String login,

        @NotNull(message = "Please provide password (Not NULL)")
        @NotBlank(message = "Please provide password")
        String password
) { }
