package com.zer0s2m.creeptenuous.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zer0s2m.creeptenuous.common.enums.UserAlready;
import com.zer0s2m.creeptenuous.common.enums.UserRole;
import com.zer0s2m.creeptenuous.common.http.ResponseError;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.repository.user.UserRepository;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagControllerApi;
import com.zer0s2m.creeptenuous.starter.test.helpers.UtilsAuthAction;
import com.zer0s2m.creeptenuous.starter.test.mock.MockUserModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagControllerApi
public class ControllerApiCreateUserTests {
    Logger logger = LogManager.getLogger(ControllerApiCreateUserTests.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    User RECORD_ADMIN = new User(
            UtilsAuthAction.LOGIN,
            "test_password",
            "test_admin@test_admin.com",
            "test_admin",
            UserRole.ROLE_ADMIN
    );

    private final String accessToken = UtilsAuthAction.builderHeader(UtilsAuthAction.generateAccessToken(true));

    MockUserModel RECORD_1 = new MockUserModel(
            "test_login",
            "test_password",
            "test_email@test.com",
            "test_name",
            "ROLE_USER"
    );

    User RECORD_ALREADY_EXISTS_LOGIN = new User(
            "test_login",
            "test_password",
            "test_email_test_email@test.com",
            "test_name"
    );

    User RECORD_ALREADY_EXISTS_EMAIL = new User(
            "test_login_test_login",
            "test_password",
            "test_email@test.com",
            "test_name"
    );

    @Test
    @Rollback
    public void createUser_success() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/user/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(RECORD_1))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isCreated());
        logger.info("Create user: " + RECORD_1);

        Assertions.assertTrue(userRepository.existsUserByLogin(RECORD_1.login()));
    }

    @Test
    @Rollback
    public void createUser_fail_userAlreadyExistsLogin() throws Exception {
        userRepository.save(RECORD_ALREADY_EXISTS_LOGIN);
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/user/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(RECORD_1))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(
                                new ResponseError(
                                        String.format(
                                                UserAlready.USER_ALREADY_EXISTS.get(),
                                                UserAlready.USER_LOGIN_EXISTS.get()
                                        ),
                                        HttpStatus.BAD_REQUEST.value()
                                )
                        )
                ));
    }

    @Test
    @Rollback
    public void createUser_fail_userAlreadyExistsEmail() throws Exception {
        userRepository.save(RECORD_ALREADY_EXISTS_EMAIL);
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/user/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(RECORD_1))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(
                                new ResponseError(
                                        String.format(
                                                UserAlready.USER_ALREADY_EXISTS.get(),
                                                UserAlready.USER_EMAIL_EXISTS.get()
                                        ),
                                        HttpStatus.BAD_REQUEST.value()
                                )
                        )
                ));
    }

}
