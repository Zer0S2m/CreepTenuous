package com.zer0s2m.CreepTenuous.api.validations;

import com.zer0s2m.CreepTenuous.helpers.BaseValidationDataApi;
import com.zer0s2m.CreepTenuous.providers.jwt.http.JwtUserRequest;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class JwtUserRequestApiValidationTests extends BaseValidationDataApi<JwtUserRequest> {
    @Test
    public void notValidLogin_fail() {
        JwtUserRequest invalidDataUser = new JwtUserRequest(null, "password");
        setErrorInvalidData(getValidator(), invalidDataUser);
    }

    @Test
    public void notValidPassword_fail() {
        JwtUserRequest invalidDataUser = new JwtUserRequest("login", null);
        setErrorInvalidData(getValidator(), invalidDataUser);
    }
}
