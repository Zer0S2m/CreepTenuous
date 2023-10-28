package com.zer0s2m.creeptenuous.api.controllers;

import com.zer0s2m.creeptenuous.api.helpers.UtilsActionForFiles;
import com.zer0s2m.creeptenuous.common.enums.Directory;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.FileSystemUtils;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagControllerApi
public class ControllerApiUploadDirectoryTests {

    Logger logger = LogManager.getLogger(ControllerApiUploadDirectoryTests.class);

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

    @Test
    public void uploadDirectory_success() throws Exception {
        String testFileZip = "test-zip.zip";

        File testFile = new File("src/test/resources/" + testFileZip);
        InputStream targetStream = new FileInputStream(testFile);

        mockMvc.perform(
                multipart("/api/v1/directory/upload")
                        .file(new MockMultipartFile(
                                "directory",
                                testFileZip,
                                "application/zip",
                                targetStream
                        ))
                        .queryParam("parents", "")
                        .queryParam("systemParents", "")
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isCreated());

        targetStream.close();

        String testPath = buildDirectoryPath.build(new ArrayList<>());
        logger.info(String.format("Upload file zip: %s", testPath + Directory.SEPARATOR.get() + testFileZip));
    }

    @Test
    public void uploadDirectory_fail_invalidPathDirectory() throws Exception {
        mockMvc.perform(
                multipart("/api/v1/directory/upload")
                        .queryParam("parents", "invalid", "path", "directory")
                        .queryParam("systemParents", "invalid", "path", "directory")
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void uploadDirectory_fail_forbiddenDirectories() throws Exception {
        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                "ROLE_USER",
                "testDirectory",
                "testDirectory",
                "testDirectory",
                new ArrayList<>());
        directoryRedisRepository.save(directoryRedis);

        String testFileZip = "test-zip.zip";
        File testFile = new File("src/test/resources/" + testFileZip);
        InputStream targetStream = new FileInputStream(testFile);

        mockMvc.perform(
                multipart("/api/v1/directory/upload")
                        .file(new MockMultipartFile(
                                "directory",
                                testFileZip,
                                "application/zip",
                                targetStream
                        ))
                        .queryParam("parents", "testDirectory")
                        .queryParam("systemParents", "testDirectory")
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isForbidden());

        targetStream.close();
        directoryRedisRepository.delete(directoryRedis);
    }

    @Test
    public void uploadDirectory_success_forbidden() throws Exception {
        UtilsActionForFiles.createDirectories(
                List.of("testDirectory"),
                buildDirectoryPath,
                logger);
        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                "ROLE_USER",
                "testDirectory",
                "testDirectory",
                "testDirectory",
                List.of(UtilsAuthAction.LOGIN));
        RightUserFileSystemObjectRedis right = new RightUserFileSystemObjectRedis(
                "testDirectory" + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.SHOW, OperationRights.UPLOAD));
        directoryRedisRepository.save(directoryRedis);
        rightUserFileSystemObjectRedisRepository.save(right);

        String testFileZip = "test-zip.zip";
        File testFile = new File("src/test/resources/" + testFileZip);
        InputStream targetStream = new FileInputStream(testFile);

        mockMvc.perform(
                multipart("/api/v1/directory/upload")
                        .file(new MockMultipartFile(
                                "directory",
                                testFileZip,
                                "application/zip",
                                targetStream
                        ))
                        .queryParam("parents", "testDirectory")
                        .queryParam("systemParents", "testDirectory")
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isCreated());

        targetStream.close();
        directoryRedisRepository.delete(directoryRedis);
        rightUserFileSystemObjectRedisRepository.delete(right);

        FileSystemUtils.deleteRecursively(Path.of(buildDirectoryPath.build(List.of("testDirectory"))));
    }

    @Test
    public void uploadDirectory_fail_isFrozenDirectories() throws Exception {
        UtilsActionForFiles.createDirectories(
                List.of("testDirectory"),
                buildDirectoryPath,
                logger);
        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                "ROLE_USER",
                "testDirectory",
                "testDirectory",
                "testDirectory",
                List.of(UtilsAuthAction.LOGIN));
        RightUserFileSystemObjectRedis right = new RightUserFileSystemObjectRedis(
                "testDirectory" + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.SHOW, OperationRights.UPLOAD));
        FrozenFileSystemObjectRedis frozenFileSystemObjectRedis = new FrozenFileSystemObjectRedis(
                "testDirectory");

        directoryRedisRepository.save(directoryRedis);
        rightUserFileSystemObjectRedisRepository.save(right);
        frozenFileSystemObjectRedisRepository.save(frozenFileSystemObjectRedis);

        String testFileZip = "test-zip.zip";
        File testFile = new File("src/test/resources/" + testFileZip);
        InputStream targetStream = new FileInputStream(testFile);

        mockMvc.perform(
                multipart("/api/v1/directory/upload")
                        .file(new MockMultipartFile(
                                "directory",
                                testFileZip,
                                "application/zip",
                                targetStream
                        ))
                        .queryParam("parents", "testDirectory")
                        .queryParam("systemParents", "testDirectory")
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isBadRequest());

        targetStream.close();
        directoryRedisRepository.delete(directoryRedis);
        frozenFileSystemObjectRedisRepository.delete(frozenFileSystemObjectRedis);
        rightUserFileSystemObjectRedisRepository.delete(right);

        FileSystemUtils.deleteRecursively(Path.of(buildDirectoryPath.build(List.of("testDirectory"))));
    }

}
