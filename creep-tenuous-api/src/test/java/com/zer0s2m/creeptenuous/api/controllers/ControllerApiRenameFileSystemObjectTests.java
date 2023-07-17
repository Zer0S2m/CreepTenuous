package com.zer0s2m.creeptenuous.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zer0s2m.creeptenuous.common.data.DataRenameFileSystemObjectApi;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FrozenFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.models.RightUserFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FrozenFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.RightUserFileSystemObjectRedisRepository;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagControllerApi
public class ControllerApiRenameFileSystemObjectTests {

    @Autowired
    private DirectoryRedisRepository directoryRedisRepository;

    @Autowired
    private RightUserFileSystemObjectRedisRepository rightUserFileSystemObjectRedisRepository;

    @Autowired
    private FrozenFileSystemObjectRedisRepository frozenFileSystemObjectRedisRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String accessToken = UtilsAuthAction.builderHeader(UtilsAuthAction.generateAccessToken());

    @Test
    public void renameFileSystemObject_success() throws Exception {
        DirectoryRedis directoryRedis = new DirectoryRedis(
                UtilsAuthAction.LOGIN,
                UtilsAuthAction.ROLE_USER,
                "test",
                "test_test",
                Path.of("test").toString(), new ArrayList<>());
        directoryRedisRepository.save(directoryRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/file-system-object/rename")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataRenameFileSystemObjectApi(
                                "test_test",
                                "new_test"

                        )))
                        .header("Authorization", accessToken))
                .andExpect(status().isOk());

        DirectoryRedis newDirectoryRedis = directoryRedisRepository.findById("test_test").get();

        Assertions.assertEquals(newDirectoryRedis.getRealNameDirectory(), "new_test");

        directoryRedisRepository.delete(directoryRedis);
    }

    @Test
    public void renameFileSystemObject_fail_forbiddenFileSystemObject() throws Exception {
        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                "ROLE_USER",
                "testDirectory",
                "testDirectory",
                "testDirectory",
                new ArrayList<>());
        directoryRedisRepository.save(directoryRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/file-system-object/rename")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(new DataRenameFileSystemObjectApi(
                                "testDirectory",
                                "new_name"
                        )))
                )
                .andExpect(status().isForbidden());

        directoryRedisRepository.delete(directoryRedis);
    }

    @Test
    public void renameFileSystemObject_success_forbidden() throws Exception {
        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                "ROLE_USER",
                "testDirectory",
                "testDirectory",
                "testDirectory",
                List.of(UtilsAuthAction.LOGIN));
        RightUserFileSystemObjectRedis right = new RightUserFileSystemObjectRedis(
                "testDirectory" + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.RENAME));
        directoryRedisRepository.save(directoryRedis);
        rightUserFileSystemObjectRedisRepository.save(right);

        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/file-system-object/rename")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(new DataRenameFileSystemObjectApi(
                                "testDirectory",
                                "new_name"
                        )))
                )
                .andExpect(status().isOk());

        directoryRedisRepository.delete(directoryRedis);
        rightUserFileSystemObjectRedisRepository.delete(right);
    }

    @Test
    public void renameFileSystemObject_fail_isFrozen() throws Exception {
        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                "ROLE_USER",
                "testDirectory",
                "testDirectory",
                "testDirectory",
                List.of(UtilsAuthAction.LOGIN));
        RightUserFileSystemObjectRedis right = new RightUserFileSystemObjectRedis(
                "testDirectory" + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.RENAME));
        FrozenFileSystemObjectRedis frozenFileSystemObjectRedis = new FrozenFileSystemObjectRedis(
                "testDirectory");

        directoryRedisRepository.save(directoryRedis);
        rightUserFileSystemObjectRedisRepository.save(right);
        frozenFileSystemObjectRedisRepository.save(frozenFileSystemObjectRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/file-system-object/rename")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(new DataRenameFileSystemObjectApi(
                                "testDirectory",
                                "new_name"
                        )))
                )
                .andExpect(status().isBadRequest());

        directoryRedisRepository.delete(directoryRedis);
        frozenFileSystemObjectRedisRepository.delete(frozenFileSystemObjectRedis);
        rightUserFileSystemObjectRedisRepository.delete(right);
    }

}
