package com.zer0s2m.CreepTenuous.api.controllers;

import com.zer0s2m.CreepTenuous.helpers.CollectionTokens;
import com.zer0s2m.CreepTenuous.models.User;
import com.zer0s2m.CreepTenuous.providers.jwt.JwtProvider;
import com.zer0s2m.CreepTenuous.providers.jwt.exceptions.messages.UserNotFoundMsg;
import com.zer0s2m.CreepTenuous.providers.jwt.exceptions.messages.UserNotValidPasswordMsg;
import com.zer0s2m.CreepTenuous.providers.jwt.http.JwtRefreshTokenRequest;
import com.zer0s2m.CreepTenuous.providers.jwt.http.JwtResponse;
import com.zer0s2m.CreepTenuous.providers.jwt.http.JwtUserRequest;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.JwtRedisDataRepository;
import com.zer0s2m.CreepTenuous.repositories.UserRepository;
import com.zer0s2m.CreepTenuous.services.user.enums.UserException;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(addFilters = false)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ControllerApiAuthTests {
    Logger logger = LogManager.getLogger(ControllerApiAuthTests.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtRedisDataRepository jwtRedisDataRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtProvider jwtProvider;

    JwtUserRequest RECORD_1 = new JwtUserRequest("test_login", "test_password");

    JwtUserRequest RECORD_INVALID_PASSWORD = new JwtUserRequest("test_login", "invalid");

    User RECORD_CREATE_USER = new User(
            "test_login",
            "test_password",
            "test_email@test.com",
            "test_name"
    );

    @Test
    @Rollback
    public void loginUser_success() throws Exception {
        userRepository.save(RECORD_CREATE_USER);

        logger.info("Create user: " + RECORD_CREATE_USER);

        MvcResult result = this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(RECORD_1))
                )
                .andExpect(status().isOk())
                .andReturn();

        String responseContentStr = result.getResponse().getContentAsString();
        DocumentContext responseContent = JsonPath.parse(responseContentStr);
        String accessToken = responseContent.read("accessToken");
        String refreshToken = responseContent.read("refreshToken");

        logger.info("Get access token: " + accessToken);
        logger.info("Get refresh token: " + refreshToken);

        Assertions.assertTrue(jwtProvider.validateAccessToken(accessToken));
        Assertions.assertTrue(jwtProvider.validateRefreshToken(refreshToken));

        jwtRedisDataRepository.deleteById(RECORD_1.login());
    }

    @Test
    public void loginUser_fail_userNotExits() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(RECORD_1))
                )
                .andExpect(status().isUnauthorized())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(
                                new UserNotFoundMsg(UserException.USER_NOT_IS_EXISTS.get())
                        )
                ));
    }

    @Test
    public void loginUser_fail_invalidPassword() throws Exception {
        userRepository.save(RECORD_CREATE_USER);

        logger.info("Create user: " + RECORD_CREATE_USER);

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(RECORD_INVALID_PASSWORD))
                )
                .andExpect(status().isUnauthorized())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(
                                new UserNotValidPasswordMsg(UserException.USER_NOT_VALID_PASSWORD.get())
                        )
                ));
    }

    @Test
    public void getAccessToken_success() throws Exception {
        userRepository.save(RECORD_CREATE_USER);

        logger.info("Create user: " + RECORD_CREATE_USER);

        CollectionTokens<String, String> tokens = getTokens();
        String refreshToken = tokens.refreshToken();

        logger.info("Get refresh token: " + refreshToken);

        Assertions.assertTrue(jwtProvider.validateRefreshToken(refreshToken));

        MvcResult resultRefreshToken = this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/auth/token")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new JwtRefreshTokenRequest(
                                refreshToken
                        )))
                )
                .andExpect(status().isOk())
                .andReturn();

        String responseContentAccessTokenStr = resultRefreshToken.getResponse().getContentAsString();
        DocumentContext responseContentAccessToken = JsonPath.parse(responseContentAccessTokenStr);
        String accessToken = responseContentAccessToken.read("accessToken");

        logger.info("Get access token: " + accessToken);

        Assertions.assertTrue(jwtProvider.validateAccessToken(accessToken));

        jwtRedisDataRepository.deleteById(RECORD_CREATE_USER.getLogin());
    }

    @Test
    public void getAccessToken_fail_notRefreshToken() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/auth/token")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new JwtRefreshTokenRequest(
                                "invalidRefreshToken"
                        )))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(
                                new JwtResponse(null, null)
                        )
                ));
    }

    @Test
    @Rollback
    public void getRefreshToken_success() throws Exception {
        userRepository.save(RECORD_CREATE_USER);

        logger.info("Create user: " + RECORD_CREATE_USER);

        CollectionTokens<String, String> tokens = getTokens();
        String refreshToken = tokens.refreshToken();

        logger.info("Get refresh token: " + refreshToken);

        Assertions.assertTrue(jwtProvider.validateRefreshToken(refreshToken));

        MvcResult result = this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/auth/refresh")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new JwtRefreshTokenRequest(
                                refreshToken
                        )))
                )
                .andExpect(status().isOk())
                .andReturn();

        String responseContentStr = result.getResponse().getContentAsString();
        DocumentContext responseContent = JsonPath.parse(responseContentStr);

        CollectionTokens<String, String> responseTokens = new CollectionTokens<>(
                responseContent.read("accessToken"),
                responseContent.read("refreshToken")
        );

        Assertions.assertTrue(jwtProvider.validateAccessToken(responseTokens.accessToken()));
        Assertions.assertTrue(jwtProvider.validateRefreshToken(responseTokens.refreshToken()));

        jwtRedisDataRepository.deleteById(RECORD_CREATE_USER.getLogin());
    }

    public CollectionTokens<String, String> getTokens() throws Exception {
        MvcResult result = this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(RECORD_1))
                )
                .andExpect(status().isOk())
                .andReturn();

        String responseContentStr = result.getResponse().getContentAsString();
        DocumentContext responseContent = JsonPath.parse(responseContentStr);

        return new CollectionTokens<>(
                responseContent.read("accessToken"),
                responseContent.read("refreshToken")
        );
    }
}
