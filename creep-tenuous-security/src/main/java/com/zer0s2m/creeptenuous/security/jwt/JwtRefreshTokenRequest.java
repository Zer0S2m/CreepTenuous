package com.zer0s2m.creeptenuous.security.jwt;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record JwtRefreshTokenRequest(

        @NotNull(message = "Please provide refresh token (Not NULL)")
        @NotBlank(message = "Please provide refresh token")
        String refreshToken

) {
}
