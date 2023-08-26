package com.zer0s2m.creeptenuous.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zer0s2m.creeptenuous.common.data.DataControlFileObjectsExclusionApi;
import com.zer0s2m.creeptenuous.common.data.DataIsDeletingFileObjectApi;
import com.zer0s2m.creeptenuous.common.data.DataTransferredUserApi;
import com.zer0s2m.creeptenuous.common.enums.UserRole;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.models.user.UserFileObjectsExclusion;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.repository.user.UserFileObjectsExclusionRepository;
import com.zer0s2m.creeptenuous.repository.user.UserRepository;
import com.zer0s2m.creeptenuous.security.jwt.http.JwtUserRequest;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@Rollback
@AutoConfigureMockMvc(addFilters = false)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagControllerApi
public class ControllerApiProfileUserTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private DirectoryRedisRepository directoryRedisRepository;

    @Autowired
    private UserFileObjectsExclusionRepository userFileObjectsExclusionRepository;

    User RECORD_CREATE_USER = new User(
            "test_login",
            "test_password",
            "test_email@test.com",
            "test_name"
    );

    User RECORD_CREATE_USER_2= new User(
            "test_login_2",
            "test_password_2",
            "test_email_2@test_2.com",
            "test_name_2"
    );

    @Test
    public void getProfileUser_success() throws Exception {
        userRepository.save(RECORD_CREATE_USER);
        String accessToken = jwtProvider.generateAccessToken(new JwtUserRequest(
                RECORD_CREATE_USER.getLogin(), "test_password"), UserRole.ROLE_USER);

        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/user/profile")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk());
    }

    @Test
    public void setIsDeletingFileObjectsSettings_success() throws Exception {
        userRepository.save(RECORD_CREATE_USER);
        String accessToken = jwtProvider.generateAccessToken(new JwtUserRequest(
                RECORD_CREATE_USER.getLogin(), "test_password"), UserRole.ROLE_USER);

        this.mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/v1/user/profile/settings/is-delete-file-objects")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(objectMapper.writeValueAsString(new DataIsDeletingFileObjectApi(
                                false
                        )))
                )
                .andExpect(status().isNoContent());
    }

    @Test
    public void setTransferredUserId_success() throws Exception {
        userRepository.save(RECORD_CREATE_USER);
        User user2 = userRepository.save(RECORD_CREATE_USER_2);
        String accessToken = jwtProvider.generateAccessToken(new JwtUserRequest(
                RECORD_CREATE_USER.getLogin(), "test_password"), UserRole.ROLE_USER);

        this.mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/v1/user/profile/settings/set-transfer-user")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(objectMapper.writeValueAsString(new DataTransferredUserApi(
                                user2.getId()
                        )))
                )
                .andExpect(status().isNoContent());
    }

    @Test
    public void setTransferredUserId_fail_userNotFound() throws Exception {
        userRepository.save(RECORD_CREATE_USER);
        String accessToken = jwtProvider.generateAccessToken(new JwtUserRequest(
                RECORD_CREATE_USER.getLogin(), "test_password"), UserRole.ROLE_USER);

        this.mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/v1/user/profile/settings/set-transfer-user")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(objectMapper.writeValueAsString(new DataTransferredUserApi(
                                123123L
                        )))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void setFileObjectsExclusion_success() throws Exception {
        userRepository.save(RECORD_CREATE_USER);
        String accessToken = jwtProvider.generateAccessToken(new JwtUserRequest(
                RECORD_CREATE_USER.getLogin(), "test_password"), UserRole.ROLE_USER);

        String systemName = UUID.randomUUID().toString();
        directoryRedisRepository.save(new DirectoryRedis(
                RECORD_CREATE_USER.getLogin(),
                "ROLE_USER",
                systemName,
                systemName,
                systemName,
                new ArrayList<>()
        ));

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/user/profile/settings/exclusions-file-objects")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(objectMapper.writeValueAsString(new DataControlFileObjectsExclusionApi(
                                List.of(systemName)
                        )))
                )
                .andExpect(status().isNoContent());

        directoryRedisRepository.deleteById(systemName);
    }

    @Test
    public void setFileObjectsExclusion_fail_notFoundFileObjects() throws Exception {
        userRepository.save(RECORD_CREATE_USER);
        String accessToken = jwtProvider.generateAccessToken(new JwtUserRequest(
                RECORD_CREATE_USER.getLogin(), "test_password"), UserRole.ROLE_USER);

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/user/profile/settings/exclusions-file-objects")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(objectMapper.writeValueAsString(new DataControlFileObjectsExclusionApi(
                                List.of(UUID.randomUUID().toString())
                        )))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteFileObjectsExclusion_success() throws Exception {
        User user = userRepository.save(RECORD_CREATE_USER);
        String accessToken = jwtProvider.generateAccessToken(new JwtUserRequest(
                RECORD_CREATE_USER.getLogin(), "test_password"), UserRole.ROLE_USER);

        UUID systemName = UUID.randomUUID();

        UserFileObjectsExclusion userFileObjectsExclusion = userFileObjectsExclusionRepository
                .save(new UserFileObjectsExclusion(systemName, user));

        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/user/profile/settings/exclusions-file-objects")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(objectMapper.writeValueAsString(new DataControlFileObjectsExclusionApi(
                                List.of(systemName.toString())
                        )))
                )
                .andExpect(status().isNoContent());

        Assertions.assertFalse(userFileObjectsExclusionRepository.existsById(userFileObjectsExclusion.getId()));
    }

}
