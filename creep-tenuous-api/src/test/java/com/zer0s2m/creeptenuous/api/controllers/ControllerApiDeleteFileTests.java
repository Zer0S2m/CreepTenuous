package com.zer0s2m.creeptenuous.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zer0s2m.creeptenuous.api.helpers.UtilsActionForFiles;
import com.zer0s2m.creeptenuous.common.components.RootPath;
import com.zer0s2m.creeptenuous.common.data.DataDeleteFileApi;
import com.zer0s2m.creeptenuous.common.enums.Directory;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.exceptions.messages.ExceptionNotDirectoryMsg;
import com.zer0s2m.creeptenuous.common.exceptions.messages.NoSuchFileExists;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.models.FrozenFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.models.RightUserFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FrozenFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.RightUserFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagControllerApi;
import com.zer0s2m.creeptenuous.starter.test.helpers.UtilsAuthAction;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.FileSystemUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagControllerApi
public class ControllerApiDeleteFileTests {

    Logger logger = LogManager.getLogger(ControllerApiDeleteFileTests.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    private RootPath rootPath;

    @Autowired
    private DirectoryRedisRepository directoryRedisRepository;

    @Autowired
    private FileRedisRepository fileRedisRepository;

    @Autowired
    private RightUserFileSystemObjectRedisRepository rightUserFileSystemObjectRedisRepository;

    @Autowired
    private FrozenFileSystemObjectRedisRepository frozenFileSystemObjectRedisRepository;

    private final String accessToken = UtilsAuthAction.builderHeader(UtilsAuthAction.generateAccessToken());

    private final String testFile1 = "testFile1.txt";

    private final String testFile2 = "testFile2.txt";

    DataDeleteFileApi RECORD_1 = new DataDeleteFileApi(testFile1, testFile1, new ArrayList<>(), new ArrayList<>());

    DataDeleteFileApi RECORD_2 = new DataDeleteFileApi(testFile2, testFile2, new ArrayList<>(), new ArrayList<>());

    @Test
    public void deleteFile_success() throws Exception {
        for (DataDeleteFileApi record : Arrays.asList(RECORD_1, RECORD_2)) {
            Path pathTestFile = UtilsActionForFiles.preparePreliminaryFiles(
                    record.fileName(), record.parents(), logger, buildDirectoryPath
            );
            Files.createFile(pathTestFile);

            this.mockMvc.perform(
                    MockMvcRequestBuilders.delete("/api/v1/file/delete")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(record))
                            .header("Authorization", accessToken)
                    )
                    .andExpect(status().isNoContent());
            Assertions.assertFalse(Files.exists(pathTestFile));

            UtilsActionForFiles.deleteFileAndWriteLog(pathTestFile, logger);
        }
    }

    @Test
    public void deleteFile_file_invalidPathDirectory() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/file/delete")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataDeleteFileApi(
                                "failTestFile",
                                "failTestFile",
                                Arrays.asList("invalid", "path", "directory"),
                                Arrays.asList("invalid", "path", "directory")
                        )))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNotFound())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(
                                new ExceptionNotDirectoryMsg(Directory.NOT_FOUND_DIRECTORY.get())
                        )
                ));
    }

    @Test
    public void deleteFile_file_notIsExists() throws Exception {
        DataDeleteFileApi INVALID_RECORD_NOT_EXISTS_FILE = new DataDeleteFileApi(
                "notExistsFileFail",
                "notExistsFileFail",
                new ArrayList<>(),
                new ArrayList<>()
        );

        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/file/delete")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(INVALID_RECORD_NOT_EXISTS_FILE))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNotFound())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(
                                new NoSuchFileExists(
                                        rootPath.getRootPath() +
                                        "/" +
                                        INVALID_RECORD_NOT_EXISTS_FILE.systemFileName())
                        )
                ));
    }

    @Test
    public void deleteFile_fail_notValidNameFile() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/file/delete")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new DataDeleteFileApi("", "", new ArrayList<>(), new ArrayList<>())
                        ))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteFile_fail_notValidParents() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/file/delete")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new DataDeleteFileApi(
                                        "testFile",
                                        "testFile",
                                        null, null
                                )
                        ))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteFile_fail_forbiddenDirectories() throws Exception {
        DataDeleteFileApi dataDeleteFileApi = new DataDeleteFileApi(
                testFile1, testFile1, List.of("testDirectory"), List.of("testDirectory"));

        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                "ROLE_USER",
                "testDirectory",
                "testDirectory",
                "testDirectory",
                new ArrayList<>());
        directoryRedisRepository.save(directoryRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/file/delete")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(dataDeleteFileApi))
                )
                .andExpect(status().isForbidden());

        directoryRedisRepository.delete(directoryRedis);
    }

    @Test
    public void deleteFile_fail_forbiddenFile() throws Exception {
        DataDeleteFileApi dataDeleteFileApi = new DataDeleteFileApi(
                testFile1, testFile1, List.of("testDirectory"), List.of("testDirectory"));

        DirectoryRedis directoryRedis = new DirectoryRedis(
                UtilsAuthAction.LOGIN,
                UtilsAuthAction.ROLE_USER,
                "testDirectory",
                "testDirectory",
                "testDirectory",
                new ArrayList<>());
        directoryRedisRepository.save(directoryRedis);
        FileRedis fileRedis = new FileRedis(
                "login",
                "ROLE_USER",
                testFile1,
                testFile1,
                testFile1,
                new ArrayList<>());
        fileRedisRepository.save(fileRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/file/delete")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(dataDeleteFileApi))
                )
                .andExpect(status().isForbidden());

        directoryRedisRepository.delete(directoryRedis);
        fileRedisRepository.delete(fileRedis);
    }

    @Test
    public void deleteFile_success_forbidden() throws Exception {
        UtilsActionForFiles.createDirectories(
                List.of("testDirectory"),
                buildDirectoryPath,
                logger);
        Files.createFile(Path.of(rootPath.getRootPath(), "testDirectory", testFile1));

        DataDeleteFileApi dataDeleteFileApi = new DataDeleteFileApi(
                testFile1, testFile1, List.of("testDirectory"), List.of("testDirectory"));

        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                UtilsAuthAction.ROLE_USER,
                "testDirectory",
                "testDirectory",
                "testDirectory",
                List.of(UtilsAuthAction.LOGIN));
        FileRedis fileRedis = new FileRedis(
                "login",
                UtilsAuthAction.ROLE_USER,
                testFile1,
                testFile1,
                testFile1,
                List.of(UtilsAuthAction.LOGIN));
        RightUserFileSystemObjectRedis rightDirectory = new RightUserFileSystemObjectRedis(
                "testDirectory" + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.SHOW));
        RightUserFileSystemObjectRedis rightFile = new RightUserFileSystemObjectRedis(
                testFile1 + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.DELETE));
        directoryRedisRepository.save(directoryRedis);
        fileRedisRepository.save(fileRedis);
        rightUserFileSystemObjectRedisRepository.saveAll(List.of(rightDirectory, rightFile));

        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/file/delete")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(dataDeleteFileApi))
                )
                .andExpect(status().isNoContent());

        directoryRedisRepository.delete(directoryRedis);
        fileRedisRepository.delete(fileRedis);
        rightUserFileSystemObjectRedisRepository.deleteAll(List.of(rightDirectory, rightFile));

        FileSystemUtils.deleteRecursively(Path.of(buildDirectoryPath.build(List.of("testDirectory"))));
    }

    @Test
    public void deleteFile_fail_isFrozenDirectory() throws Exception {
        DataDeleteFileApi dataDeleteFileApi = new DataDeleteFileApi(
                testFile1, testFile1, List.of("testDirectory"), List.of("testDirectory"));

        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                UtilsAuthAction.ROLE_USER,
                "testDirectory",
                "testDirectory",
                "testDirectory",
                List.of(UtilsAuthAction.LOGIN));
        RightUserFileSystemObjectRedis rightDirectory = new RightUserFileSystemObjectRedis(
                "testDirectory" + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.SHOW));
        FrozenFileSystemObjectRedis frozenFileSystemObjectRedis = new FrozenFileSystemObjectRedis(
                "testDirectory");

        directoryRedisRepository.save(directoryRedis);
        rightUserFileSystemObjectRedisRepository.save(rightDirectory);
        frozenFileSystemObjectRedisRepository.save(frozenFileSystemObjectRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/file/delete")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(dataDeleteFileApi))
                )
                .andExpect(status().isBadRequest());

        directoryRedisRepository.delete(directoryRedis);
        rightUserFileSystemObjectRedisRepository.delete(rightDirectory);
        frozenFileSystemObjectRedisRepository.delete(frozenFileSystemObjectRedis);
    }

    @Test
    public void deleteFile_fail_isFrozenFile() throws Exception {
        DataDeleteFileApi dataDeleteFileApi = new DataDeleteFileApi(
                testFile1, testFile1, new ArrayList<>(), new ArrayList<>());

        FileRedis fileRedis = new FileRedis(
                "login",
                UtilsAuthAction.ROLE_USER,
                testFile1,
                testFile1,
                testFile1,
                List.of(UtilsAuthAction.LOGIN));
        RightUserFileSystemObjectRedis rightFile = new RightUserFileSystemObjectRedis(
                testFile1 + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.DELETE));
        FrozenFileSystemObjectRedis frozenFileSystemObjectRedis = new FrozenFileSystemObjectRedis(
                testFile1);

        fileRedisRepository.save(fileRedis);
        rightUserFileSystemObjectRedisRepository.save(rightFile);
        frozenFileSystemObjectRedisRepository.save(frozenFileSystemObjectRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/file/delete")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(dataDeleteFileApi))
                )
                .andExpect(status().isBadRequest());

        fileRedisRepository.delete(fileRedis);
        rightUserFileSystemObjectRedisRepository.delete(rightFile);
        frozenFileSystemObjectRedisRepository.delete(frozenFileSystemObjectRedis);
    }

}
