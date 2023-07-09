package com.zer0s2m.creeptenuous.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zer0s2m.creeptenuous.common.data.DataControlFileSystemObjectApi;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FrozenFileSystemObjectRedisRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(addFilters = false)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagControllerApi
public class ControllerApiControlFileSystemObjectTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DirectoryRedisRepository directoryRedisRepository;

    @Autowired
    private FrozenFileSystemObjectRedisRepository frozenFileSystemObjectRedisRepository;

    private final String accessToken = UtilsAuthAction.builderHeader(UtilsAuthAction.generateAccessToken());

    @Test
    public void freezingFileSystemObject_success() throws Exception {
        final String systemName = UUID.randomUUID().toString();
        final DirectoryRedis directoryRedis = directoryRedisRepository.save(new DirectoryRedis(
                UtilsAuthAction.LOGIN,
                UtilsAuthAction.ROLE_USER,
                systemName,
                systemName,
                systemName,
                new ArrayList<>()
        ));

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/file-system-object/freezing")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataControlFileSystemObjectApi(
                                systemName
                        )))
                        .header("Authorization",  accessToken)
                )
                .andExpect(status().isNoContent());

        Assertions.assertTrue(frozenFileSystemObjectRedisRepository.existsById(systemName));

        directoryRedisRepository.delete(directoryRedis);
        frozenFileSystemObjectRedisRepository.deleteById(systemName);
    }

    @Test
    public void freezingFileSystemObject_fail_doesNotExists() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/file-system-object/freezing")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataControlFileSystemObjectApi(
                                "not_found_file_system_object"
                        )))
                        .header("Authorization",  accessToken)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void unfreezingFileSystemObject_success() throws Exception {
        final String systemName = UUID.randomUUID().toString();
        final DirectoryRedis directoryRedis = directoryRedisRepository.save(new DirectoryRedis(
                UtilsAuthAction.LOGIN,
                UtilsAuthAction.ROLE_USER,
                systemName,
                systemName,
                systemName,
                new ArrayList<>()
        ));

        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/file-system-object/unfreezing")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataControlFileSystemObjectApi(
                                systemName
                        )))
                        .header("Authorization",  accessToken)
                )
                .andExpect(status().isNoContent());

        Assertions.assertFalse(frozenFileSystemObjectRedisRepository.existsById(systemName));

        directoryRedisRepository.delete(directoryRedis);
        frozenFileSystemObjectRedisRepository.deleteById(systemName);
    }

    @Test
    public void unfreezingFileSystemObject_fail_doesNotExists() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/file-system-object/unfreezing")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataControlFileSystemObjectApi(
                                "not_found_file_system_object"
                        )))
                        .header("Authorization",  accessToken)
                )
                .andExpect(status().isNotFound());
    }

}
