package com.zer0s2m.creeptenuous.api.controllers;

import com.zer0s2m.creeptenuous.common.enums.UserRole;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.repository.user.UserRepository;
import com.zer0s2m.creeptenuous.security.jwt.http.JwtUserRequest;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.services.security.GeneratePasswordImpl;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagControllerApi;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(addFilters = false)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagControllerApi
public class ControllerApiControlUserTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private GeneratePasswordImpl generatePassword;

    User RECORD_CREATE_USER = new User(
            "test_admin",
            null,
            "test_admin@test_admin.com",
            "test_admin",
            UserRole.ROLE_ADMIN
    );

    @Test
    @Rollback
    public void getAllUsersControl_success() throws Exception {
        RECORD_CREATE_USER.setPassword(generatePassword.generation("test_password"));
        userRepository.save(RECORD_CREATE_USER);
        String accessToken = jwtProvider.generateAccessToken(new JwtUserRequest(
                RECORD_CREATE_USER.getLogin(), "test_password"), UserRole.ROLE_ADMIN);

        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/user/control/list")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk());
    }

}
