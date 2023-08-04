package com.zer0s2m.creeptenuous.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zer0s2m.creeptenuous.common.data.DataControlShortcutFileSystemObjectApi;
import com.zer0s2m.creeptenuous.models.common.ShortcutFileSystemObject;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.repository.common.ShortcutFileSystemObjectRepository;
import com.zer0s2m.creeptenuous.repository.user.UserRepository;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagControllerApi;
import com.zer0s2m.creeptenuous.starter.test.helpers.UtilsAuthAction;
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
public class ControllerApiShortcutFileSystemObjectsUserTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShortcutFileSystemObjectRepository shortcutFileSystemObjectRepository;

    @Autowired
    private DirectoryRedisRepository directoryRedisRepository;

    User RECORD_CREATE_USER = new User(
            "test_login",
            "test_password",
            "test_email@test.com",
            "test_name"
    );

    UUID systemName1 = UUID.randomUUID();

    UUID systemName2 = UUID.randomUUID();

    @Autowired
    private ObjectMapper objectMapper;

    private final String accessToken = UtilsAuthAction.builderHeader(UtilsAuthAction.generateAccessToken());

    @Test
    public void create_success() throws Exception {
        userRepository.save(RECORD_CREATE_USER);

        directoryRedisRepository.save(new DirectoryRedis(
                UtilsAuthAction.LOGIN,
                UtilsAuthAction.ROLE_USER,
                systemName1.toString(),
                systemName1.toString(),
                systemName1.toString(),
                new ArrayList<>()
        ));
        directoryRedisRepository.save(new DirectoryRedis(
                UtilsAuthAction.LOGIN,
                UtilsAuthAction.ROLE_USER,
                systemName2.toString(),
                systemName2.toString(),
                systemName2.toString(),
                new ArrayList<>()
        ));

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/common/shortcut/file-system-object")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataControlShortcutFileSystemObjectApi(
                                systemName1.toString(),
                                systemName2.toString()
                        )))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNoContent());

        directoryRedisRepository.deleteAllById(List.of(
                systemName1.toString(), systemName2.toString()
        ));
    }

    @Test
    public void create_fail_fileObjectNotExists() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/common/shortcut/file-system-object")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataControlShortcutFileSystemObjectApi(
                                systemName1.toString(),
                                systemName2.toString()
                        )))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNotFound());

        directoryRedisRepository.save(new DirectoryRedis(
                UtilsAuthAction.LOGIN,
                UtilsAuthAction.ROLE_USER,
                systemName1.toString(),
                systemName1.toString(),
                systemName1.toString(),
                new ArrayList<>()
        ));
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/common/shortcut/file-system-object")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataControlShortcutFileSystemObjectApi(
                                systemName2.toString(),
                                systemName1.toString()
                        )))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNotFound());
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/common/shortcut/file-system-object")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataControlShortcutFileSystemObjectApi(
                                systemName1.toString(),
                                systemName2.toString()
                        )))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNotFound());

        directoryRedisRepository.deleteById(systemName1.toString());
    }

    @Test
    public void delete_success() throws Exception {
        User user = userRepository.save(RECORD_CREATE_USER);
        ShortcutFileSystemObject object = shortcutFileSystemObjectRepository.save(new ShortcutFileSystemObject(
                user,
                systemName1,
                systemName2
        ));

        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/common/shortcut/file-system-object")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataControlShortcutFileSystemObjectApi(
                                systemName2.toString(),
                                systemName1.toString()
                        )))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNoContent());

        Assertions.assertFalse(shortcutFileSystemObjectRepository.existsById(object.getId()));
    }

}
