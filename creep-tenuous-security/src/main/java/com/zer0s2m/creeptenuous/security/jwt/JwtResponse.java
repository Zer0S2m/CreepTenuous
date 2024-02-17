package com.zer0s2m.creeptenuous.security.jwt;

public record JwtResponse(

        String accessToken,

        String refreshToken

) {
}
