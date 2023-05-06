package com.zer0s2m.CreepTenuous.api.validations;

import com.zer0s2m.CreepTenuous.helpers.BaseValidationDataApi;
import com.zer0s2m.CreepTenuous.helpers.TestTagValidationApi;
import com.zer0s2m.CreepTenuous.providers.jwt.http.JwtRefreshTokenRequest;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagValidationApi
public class JwtRefreshTokenRequestApiValidationTests extends BaseValidationDataApi<JwtRefreshTokenRequest> {
    @Test
    public void notValidLogin_fail() {
        JwtRefreshTokenRequest invalidDataToken = new JwtRefreshTokenRequest(null);
        setErrorInvalidData(getValidator(), invalidDataToken);
    }
}
