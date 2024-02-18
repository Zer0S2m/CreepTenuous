package com.zer0s2m.creeptenuous.api.controllers;

import com.zer0s2m.creeptenuous.api.helpers.UtilsActionForFiles;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FrozenFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.models.RightUserFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FrozenFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.RightUserFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.core.services.ServiceBuildDirectoryPath;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagControllerApi;
import com.zer0s2m.creeptenuous.starter.test.helpers.UtilsAuthAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.opc.ContentTypes;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.FileSystemUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagControllerApi
public class ControllerApiUploadFileTests {

    Logger logger = LogManager.getLogger(ControllerApiUploadFileTests.class);

    @Autowired
    private MockMvc mockMvc;

    private final ServiceBuildDirectoryPath buildDirectoryPath = new ServiceBuildDirectoryPath();

    @Autowired
    private DirectoryRedisRepository directoryRedisRepository;

    @Autowired
    private RightUserFileSystemObjectRedisRepository rightUserFileSystemObjectRedisRepository;

    @Autowired
    private FrozenFileSystemObjectRedisRepository frozenFileSystemObjectRedisRepository;

    private final String accessToken = UtilsAuthAction.builderHeader(UtilsAuthAction.generateAccessToken());

    private final String nameTestFile1 = "test_image_1.jpeg";

    private final String nameTestFile2 = "test_image_2.jpeg";

    @Test
    public void uploadOneFile_success() throws Exception {
        InputStream targetStream = this.getClass().getResourceAsStream("/" + nameTestFile1);

        UtilsActionForFiles.preparePreliminaryFiles(nameTestFile1, new ArrayList<>(), logger, buildDirectoryPath);

        mockMvc.perform(
                multipart("/api/v1/file/upload")
                        .file(getMockFile(nameTestFile1, targetStream))
                        .param("parents", "")
                        .param("systemParents", "")
                        .header("Authorization", accessToken)
        )
                .andExpect(status().isCreated());

        targetStream.close();
    }

    @Test
    public void uploadMoreOneFile_success() throws Exception {
        InputStream targetStream1 = this.getClass().getResourceAsStream("/" + nameTestFile1);
        InputStream targetStream2 = this.getClass().getResourceAsStream("/" + nameTestFile2);

        mockMvc.perform(
                multipart("/api/v1/file/upload")
                        .file(getMockFile(nameTestFile1, targetStream1))
                        .file(getMockFile(nameTestFile2, targetStream2))
                        .param("parents", "")
                        .param("systemParents", "")
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isCreated());

        targetStream1.close();
        targetStream2.close();

        UtilsActionForFiles.preparePreliminaryFiles(nameTestFile1, new ArrayList<>(), logger, buildDirectoryPath);
        UtilsActionForFiles.preparePreliminaryFiles(nameTestFile2, new ArrayList<>(), logger, buildDirectoryPath);
    }

    protected MockMultipartFile getMockFile(String nameFile, InputStream stream) throws IOException {
        return new MockMultipartFile(
                "files",
                nameFile,
                ContentTypes.IMAGE_JPEG,
                stream
        );
    }

    @Test
    public void uploadFile_fail_invalidPathDirectory() throws Exception {
        InputStream targetStream = this.getClass().getResourceAsStream("/" + nameTestFile2);

        mockMvc.perform(
                multipart("/api/v1/file/upload")
                        .file(getMockFile(nameTestFile2, targetStream))
                        .param("parents", "invalid", "path", "directory")
                        .param("systemParents", "invalid", "path", "directory")
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNotFound());

        targetStream.close();

        Path pathTestUploadFile = UtilsActionForFiles.preparePreliminaryFiles(
                nameTestFile2, new ArrayList<>(), logger, buildDirectoryPath
        );
        Assertions.assertFalse(Files.exists(pathTestUploadFile));
    }

    @Test
    public void uploadFile_fail_forbiddenDirectories() throws Exception {
        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                "ROLE_USER",
                "testDirectory",
                "testDirectory",
                "testDirectory",
                new ArrayList<>());
        directoryRedisRepository.save(directoryRedis);

        InputStream targetStream = this.getClass().getResourceAsStream("/" + nameTestFile2);

        mockMvc.perform(
                        multipart("/api/v1/file/upload")
                                .file(getMockFile(nameTestFile2, targetStream))
                                .param("parents", "testDirectory")
                                .param("systemParents", "testDirectory")
                                .header("Authorization", accessToken)
                )
                .andExpect(status().isForbidden());

        targetStream.close();
        directoryRedisRepository.delete(directoryRedis);
    }

    @Test
    public void uploadFile_success_forbidden() throws Exception {
        UtilsActionForFiles.createDirectories(
                List.of("testFolder"),
                buildDirectoryPath,
                logger);
        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                "ROLE_USER",
                "testFolder",
                "testFolder",
                "testFolder",
                List.of(UtilsAuthAction.LOGIN));
        RightUserFileSystemObjectRedis right = new RightUserFileSystemObjectRedis(
                "testFolder" + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.SHOW, OperationRights.UPLOAD));
        directoryRedisRepository.save(directoryRedis);
        rightUserFileSystemObjectRedisRepository.save(right);

        InputStream targetStream = this.getClass().getResourceAsStream("/" + nameTestFile2);

        mockMvc.perform(
                multipart("/api/v1/file/upload")
                        .file(getMockFile(nameTestFile2, targetStream))
                        .param("parents", "testFolder")
                        .param("systemParents", "testFolder")
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isCreated());

        targetStream.close();
        directoryRedisRepository.delete(directoryRedis);
        rightUserFileSystemObjectRedisRepository.delete(right);

        FileSystemUtils.deleteRecursively(Path.of(buildDirectoryPath.build(List.of("testFolder"))));
    }

    @Test
    public void uploadFile_fail_isFrozenDirectories() throws Exception {
        UtilsActionForFiles.createDirectories(
                List.of("testFolder"),
                buildDirectoryPath,
                logger);
        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                "ROLE_USER",
                "testFolder",
                "testFolder",
                "testFolder",
                List.of(UtilsAuthAction.LOGIN));
        RightUserFileSystemObjectRedis right = new RightUserFileSystemObjectRedis(
                "testFolder" + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.SHOW, OperationRights.UPLOAD));
        FrozenFileSystemObjectRedis frozenFileSystemObjectRedis = new FrozenFileSystemObjectRedis(
                "testFolder");

        frozenFileSystemObjectRedisRepository.save(frozenFileSystemObjectRedis);
        directoryRedisRepository.save(directoryRedis);
        rightUserFileSystemObjectRedisRepository.save(right);

        InputStream targetStream = this.getClass().getResourceAsStream("/" + nameTestFile2);

        mockMvc.perform(
                multipart("/api/v1/file/upload")
                        .file(getMockFile(nameTestFile2, targetStream))
                        .param("parents", "testFolder")
                        .param("systemParents", "testFolder")
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isBadRequest());

        targetStream.close();
        directoryRedisRepository.delete(directoryRedis);
        rightUserFileSystemObjectRedisRepository.delete(right);
        frozenFileSystemObjectRedisRepository.delete(frozenFileSystemObjectRedis);

        FileSystemUtils.deleteRecursively(Path.of(buildDirectoryPath.build(List.of("testFolder"))));
    }

}
