package com.zer0s2m.creeptenuous.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zer0s2m.creeptenuous.api.helpers.UtilsActionForFiles;
import com.zer0s2m.creeptenuous.common.data.DataCreateFileApi;
import com.zer0s2m.creeptenuous.common.enums.Directory;
import com.zer0s2m.creeptenuous.common.enums.ExceptionFile;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.enums.TypeFile;
import com.zer0s2m.creeptenuous.common.http.ResponseError;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FrozenFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.models.RightUserFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FrozenFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.RightUserFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagControllerApi;
import com.zer0s2m.creeptenuous.starter.test.helpers.UtilsAuthAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.FileSystemUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.ArrayList;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagControllerApi
public class ControllerApiCreateFileTests {

    Logger logger = LogManager.getLogger(ControllerApiCreateFileTests.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final ServiceBuildDirectoryPath buildDirectoryPath = new ServiceBuildDirectoryPath();

    @Autowired
    private DirectoryRedisRepository directoryRedisRepository;

    @Autowired
    private RightUserFileSystemObjectRedisRepository rightUserFileSystemObjectRedisRepository;

    @Autowired
    private FrozenFileSystemObjectRedisRepository frozenFileSystemObjectRedisRepository;

    private final String accessToken = UtilsAuthAction.builderHeader(UtilsAuthAction.generateAccessToken());

    DataCreateFileApi RECORD_1 = new DataCreateFileApi(
            1, UUID.randomUUID().toString(), new ArrayList<>(), new ArrayList<>());

    DataCreateFileApi RECORD_2 = new DataCreateFileApi(
            2, UUID.randomUUID().toString(), new ArrayList<>(), new ArrayList<>());

    DataCreateFileApi RECORD_3 = new DataCreateFileApi(
            3, UUID.randomUUID().toString(), new ArrayList<>(), new ArrayList<>());

    @Test
    public void createFile_success() throws Exception {
        for (DataCreateFileApi record : Arrays.asList(RECORD_1, RECORD_2, RECORD_3)) {
            this.mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/file/create")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
                            .content(objectMapper.writeValueAsString(record))
            )
                    .andExpect(status().isCreated());
        }
    }

    @Test
    public void createFile_fail_invalidTypeFile() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/file/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(new DataCreateFileApi(
                                9999,
                                "failFile",
                                new ArrayList<>(),
                                new ArrayList<>()
                        )))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(new ResponseError(
                                ExceptionFile.NOT_FOUND_TYPE_FILE.get(), HttpStatus.BAD_REQUEST.value()))
                ));
    }

    @Deprecated
    public void createFile_fail_invalidPathDirectory() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/file/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(new DataCreateFileApi(
                                1,
                                "failFile",
                                Arrays.asList("invalid", "path", "directory"),
                                Arrays.asList("invalid", "path", "directory")
                        )))
                )
                .andExpect(status().isNotFound())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(new ResponseError(
                                Directory.NOT_FOUND_DIRECTORY.get(), HttpStatus.NOT_FOUND.value()))
                ));
    }

    @Deprecated
    public void createFile_fail_fileIsExists() throws Exception {
        Path testFile = UtilsActionForFiles.preparePreliminaryFiles(
                "testFile_4" + "." + TypeFile.TXT.getExtension(), new ArrayList<>(), logger, buildDirectoryPath
        );
        Files.createFile(testFile);

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/file/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(
                                new DataCreateFileApi(
                                        1,
                                        "testFile_4",
                                        new ArrayList<>(),
                                        new ArrayList<>())
                        ))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(new ResponseError(
                                ExceptionFile.FILE_ALREADY_EXISTS.get(), HttpStatus.BAD_REQUEST.value()))
                ));

        logger.info("Is deleted file for tests: " + Files.deleteIfExists(testFile) + " (" + testFile + ")");
    }

    @Test
    public void createFile_fail_notValidNameFile() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/file/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(
                                new DataCreateFileApi(1, "", new ArrayList<>(), new ArrayList<>())
                        ))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createFile_fail_notValidTypeFile() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/file/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(
                                new DataCreateFileApi(
                                        null,
                                        "testFile",
                                        new ArrayList<>(),
                                        new ArrayList<>()
                                )
                        ))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createFile_fail_notValidParents() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/file/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(
                                new DataCreateFileApi(
                                        1,
                                        "testFile",
                                        null,
                                        null)
                        ))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createFile_fail_forbidden() throws Exception {
        DataCreateFileApi dataCreateFileApi = new DataCreateFileApi(
                1,
                "testFile",
                List.of("testDirectory"),
                List.of("testDirectory"));

        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                "ROLE_USER",
                "testDirectory",
                "testDirectory",
                "testDirectory",
                new ArrayList<>());
        directoryRedisRepository.save(directoryRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/file/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(dataCreateFileApi))
                )
                .andExpect(status().isForbidden());

        directoryRedisRepository.delete(directoryRedis);
    }

    @Test
    public void createFile_success_forbidden() throws Exception {
        UtilsActionForFiles.createDirectories(
                List.of("testDirectory"),
                buildDirectoryPath,
                logger);

        final String fileName = UUID.randomUUID().toString();
        DataCreateFileApi dataCreateFileApi = new DataCreateFileApi(
                1,
                fileName,
                List.of("testDirectory"),
                List.of("testDirectory"));
        RightUserFileSystemObjectRedis right = new RightUserFileSystemObjectRedis(
                "testDirectory" + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.CREATE));
        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                UtilsAuthAction.ROLE_USER,
                "testDirectory",
                "testDirectory",
                "testDirectory",
                List.of(UtilsAuthAction.LOGIN));

        rightUserFileSystemObjectRedisRepository.save(right);
        directoryRedisRepository.save(directoryRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/file/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(dataCreateFileApi))
                )
                .andExpect(status().isCreated());

        directoryRedisRepository.delete(directoryRedis);
        rightUserFileSystemObjectRedisRepository.delete(right);

        FileSystemUtils.deleteRecursively(Path.of(buildDirectoryPath.build(List.of("testDirectory"))));
    }

    @Test
    public void createFile_fail_isFrozenDirectories() throws Exception {
        UtilsActionForFiles.createDirectories(
                List.of("testDirectory"),
                buildDirectoryPath,
                logger);

        final String fileName = UUID.randomUUID().toString();
        DataCreateFileApi dataCreateFileApi = new DataCreateFileApi(
                1,
                fileName,
                List.of("testDirectory"),
                List.of("testDirectory"));

        RightUserFileSystemObjectRedis right = new RightUserFileSystemObjectRedis(
                "testDirectory" + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.CREATE, OperationRights.SHOW));
        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                UtilsAuthAction.ROLE_USER,
                "testDirectory",
                "testDirectory",
                "testDirectory",
                List.of(UtilsAuthAction.LOGIN));
        FrozenFileSystemObjectRedis frozenFileSystemObjectRedis = new FrozenFileSystemObjectRedis(
                "testDirectory");

        rightUserFileSystemObjectRedisRepository.save(right);
        directoryRedisRepository.save(directoryRedis);
        frozenFileSystemObjectRedisRepository.save(frozenFileSystemObjectRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/file/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(dataCreateFileApi))
                )
                .andExpect(status().isBadRequest());

        directoryRedisRepository.delete(directoryRedis);
        rightUserFileSystemObjectRedisRepository.delete(right);
        frozenFileSystemObjectRedisRepository.delete(frozenFileSystemObjectRedis);

        FileSystemUtils.deleteRecursively(Path.of(buildDirectoryPath.build(List.of("testDirectory"))));

    }

}
