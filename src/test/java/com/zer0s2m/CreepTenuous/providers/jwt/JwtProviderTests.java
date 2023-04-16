package com.zer0s2m.CreepTenuous.providers.jwt;

import com.zer0s2m.CreepTenuous.providers.jwt.http.JwtUserRequest;
import com.zer0s2m.CreepTenuous.services.user.enums.UserRole;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class JwtProviderTests {
    JwtProvider jwtProvider;

    JwtUserRequest RECORD_1 = new JwtUserRequest("login", "password");

    @BeforeEach
    void init() {
        jwtProvider = new JwtProvider(
                "EnmtYa94dFCsSAc8iqMeR0JXPjNBrSydu6T02QOG4VxhpLv3DAZoU7fzHbIlK5wc1gkW",
                "uQCSIwnVg1MolyLEXKb4PBHlR8c1Y7jA0rTzv5b6FxsfaGJtqZd9NOg27Dh3e6UkiWpm"
        );
    }

    @Test
    public void generateAccessToken_success() {
        String accessToken = jwtProvider.generateAccessToken(RECORD_1, UserRole.ROLE_USER);
        Assertions.assertTrue(jwtProvider.validateAccessToken(accessToken));
    }

    @Test
    public void generateRefreshToken_success() {
        String refreshToken = jwtProvider.generateRefreshToken(RECORD_1);
        Assertions.assertTrue(jwtProvider.validateRefreshToken(refreshToken));
    }

    @Test
    public void validateAccessToken_fail() {
        Assertions.assertFalse(jwtProvider.validateAccessToken("invalidAccessToken"));
    }

    @Test
    public void validateRefreshToken_fail() {
        Assertions.assertFalse(jwtProvider.validateRefreshToken("invalidRefreshToken"));
    }
}
