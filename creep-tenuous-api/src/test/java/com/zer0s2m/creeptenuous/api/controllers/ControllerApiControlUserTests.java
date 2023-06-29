package com.zer0s2m.creeptenuous.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zer0s2m.creeptenuous.common.data.DataBlockUserApi;
import com.zer0s2m.creeptenuous.common.data.DataDeleteUserApi;
import com.zer0s2m.creeptenuous.common.enums.UserRole;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.repository.user.UserRepository;
import com.zer0s2m.creeptenuous.security.jwt.http.JwtUserRequest;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.services.security.GeneratePasswordImpl;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagControllerApi;
import org.junit.jupiter.api.Assertions;
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
    private ObjectMapper objectMapper;

    @Autowired
    private GeneratePasswordImpl generatePassword;

    User RECORD_CREATE_USER = new User(
            "test_admin",
            null,
            "test_admin@test_admin.com",
            "test_admin",
            UserRole.ROLE_ADMIN
    );

    User RECORD_DELETE_USER = new User(
            "test_login",
            null,
            "test_login@test_login.com",
            "test_login",
            UserRole.ROLE_USER
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

    @Test
    @Rollback
    public void deleteUserByLoginControl_success() throws Exception {
        RECORD_CREATE_USER.setPassword(generatePassword.generation("test_password"));
        RECORD_DELETE_USER.setPassword(generatePassword.generation("test_password"));
        userRepository.save(RECORD_CREATE_USER);
        userRepository.save(RECORD_DELETE_USER);
        String accessToken = jwtProvider.generateAccessToken(new JwtUserRequest(
                RECORD_CREATE_USER.getLogin(), "test_password"), UserRole.ROLE_ADMIN);

        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/user/control/delete")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataDeleteUserApi(RECORD_DELETE_USER.getLogin())))
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isNoContent());

        Assertions.assertFalse(userRepository.existsUserByLogin(RECORD_DELETE_USER.getLogin()));
    }

    @Test
    @Rollback
    public void blockUserByLoginControl_success() throws Exception {
        RECORD_CREATE_USER.setPassword(generatePassword.generation("test_password"));
        RECORD_DELETE_USER.setPassword(generatePassword.generation("test_password"));
        userRepository.save(RECORD_CREATE_USER);
        userRepository.save(RECORD_DELETE_USER);

        String accessToken = jwtProvider.generateAccessToken(new JwtUserRequest(
                RECORD_CREATE_USER.getLogin(), "test_password"), UserRole.ROLE_ADMIN);

        this.mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/v1/user/control/block")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataBlockUserApi(
                                RECORD_DELETE_USER.getLogin())))
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isNoContent());

        final User user = userRepository.findByLogin(RECORD_DELETE_USER.getLogin());
        Assertions.assertFalse(user.isAccountNonLocked());
    }

    @Test
    @Rollback
    public void blockUserByLoginControl_fail_notFoundUser() throws Exception {
        RECORD_CREATE_USER.setPassword(generatePassword.generation("test_password"));
        userRepository.save(RECORD_CREATE_USER);

        String accessToken = jwtProvider.generateAccessToken(new JwtUserRequest(
                RECORD_CREATE_USER.getLogin(), "test_password"), UserRole.ROLE_ADMIN);

        this.mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/v1/user/control/block")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataBlockUserApi(
                                "not_found_user")))
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @Rollback
    public void blockUserByLoginControl_fail_selfBlock() throws Exception {
        RECORD_CREATE_USER.setPassword(generatePassword.generation("test_password"));
        userRepository.save(RECORD_CREATE_USER);

        String accessToken = jwtProvider.generateAccessToken(new JwtUserRequest(
                RECORD_CREATE_USER.getLogin(), "test_password"), UserRole.ROLE_ADMIN);

        this.mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/v1/user/control/block")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataBlockUserApi(
                                RECORD_CREATE_USER.getLogin())))
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isBadRequest());
    }

}
