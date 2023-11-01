package com.zer0s2m.creeptenuous.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zer0s2m.creeptenuous.api.helpers.UtilsActionForFiles;
import com.zer0s2m.creeptenuous.common.components.RootPath;
import com.zer0s2m.creeptenuous.common.data.DataMoveDirectoryApi;
import com.zer0s2m.creeptenuous.common.enums.MethodMoveDirectory;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
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
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagControllerApi
public class ControllerApiMoveDirectoryTests {

    Logger logger = LogManager.getLogger(ControllerApiMoveDirectoryTests.class);

    private final ServiceBuildDirectoryPath serviceBuildDirectoryPath = new ServiceBuildDirectoryPath();

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private final RootPath rootPath = new RootPath();

    @Autowired
    private DirectoryRedisRepository directoryRedisRepository;

    @Autowired
    private FileRedisRepository fileRedisRepository;

    @Autowired
    private RightUserFileSystemObjectRedisRepository rightUserFileSystemObjectRedisRepository;

    @Autowired
    private FrozenFileSystemObjectRedisRepository frozenFileSystemObjectRedisRepository;

    private final String accessToken = UtilsAuthAction.builderHeader(UtilsAuthAction.generateAccessToken());

    List<String> DIRECTORIES_1 = List.of("test_folder1");

    List<String> DIRECTORIES_2 = List.of("test_folder1", "test_folder2");

    List<String> DIRECTORIES_3 = List.of("test_folder3");

    @Test
    public void moveDirectoryFolder_success() throws Exception {
        UtilsActionForFiles.createDirectories(DIRECTORIES_3, serviceBuildDirectoryPath, logger);
        Path pathTestFile1 =  UtilsActionForFiles.preparePreliminaryFilesForCopyDirectories(
                serviceBuildDirectoryPath,
                logger,
                DIRECTORIES_1,
                "test_file1.txt"
        );
        Path pathTestFile2 = UtilsActionForFiles.preparePreliminaryFilesForCopyDirectories(
                serviceBuildDirectoryPath,
                logger,
                DIRECTORIES_2,
                "test_file2.txt"
        );

        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/directory/move")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(new DataMoveDirectoryApi(
                                new ArrayList<>(),
                                new ArrayList<>(),
                                List.of(DIRECTORIES_3.get(0)),
                                List.of(DIRECTORIES_3.get(0)),
                                DIRECTORIES_1.get(0),
                                DIRECTORIES_1.get(0),
                                MethodMoveDirectory.FOLDER.getMethod()
                        )))
                )
                .andExpect(status().isNoContent());

        Path newPathTestFile1 = Path.of(
                serviceBuildDirectoryPath.build(DIRECTORIES_3),
                DIRECTORIES_1.get(0),
                "test_file1.txt"
        );
        Path newPathTestFile2 = Path.of(
                serviceBuildDirectoryPath.build(DIRECTORIES_3),
                DIRECTORIES_1.get(0),
                "test_folder2",
                "test_file2.txt"
        );
        Assertions.assertTrue(Files.exists(newPathTestFile1));
        Assertions.assertTrue(Files.exists(newPathTestFile2));
        Assertions.assertFalse(Files.exists(pathTestFile1));
        Assertions.assertFalse(Files.exists(pathTestFile2));

        FileSystemUtils.deleteRecursively(Path.of(serviceBuildDirectoryPath.build(DIRECTORIES_3)));
    }

    @Test
    public void moveDirectoryContent_success() throws Exception {
        UtilsActionForFiles.createDirectories(DIRECTORIES_3, serviceBuildDirectoryPath, logger);
        Path pathTestFile1 =  UtilsActionForFiles.preparePreliminaryFilesForCopyDirectories(
                serviceBuildDirectoryPath,
                logger,
                DIRECTORIES_1,
                "test_file1.txt"
        );
        Path pathTestFile2 = UtilsActionForFiles.preparePreliminaryFilesForCopyDirectories(
                serviceBuildDirectoryPath,
                logger,
                DIRECTORIES_2,
                "test_file2.txt"
        );

        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/directory/move")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(new DataMoveDirectoryApi(
                                new ArrayList<>(),
                                new ArrayList<>(),
                                List.of(DIRECTORIES_3.get(0)),
                                List.of(DIRECTORIES_3.get(0)),
                                DIRECTORIES_1.get(0),
                                DIRECTORIES_1.get(0),
                                MethodMoveDirectory.CONTENT.getMethod()
                        )))
                )
                .andExpect(status().isNoContent());

        Path newPathTestFile1 = Path.of(
                serviceBuildDirectoryPath.build(DIRECTORIES_3),
                "test_file1.txt"
        );
        Path newPathTestFile2 = Path.of(
                serviceBuildDirectoryPath.build(DIRECTORIES_3),
                "test_folder2",
                "test_file2.txt"
        );
        Assertions.assertTrue(Files.exists(newPathTestFile1));
        Assertions.assertTrue(Files.exists(newPathTestFile2));
        Assertions.assertFalse(Files.exists(pathTestFile1));
        Assertions.assertFalse(Files.exists(pathTestFile2));

        FileSystemUtils.deleteRecursively(Path.of(serviceBuildDirectoryPath.build(DIRECTORIES_3)));
    }

    @Test
    public void moveDirectory_fail_invalidPathDirectory() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/directory/move")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(new DataMoveDirectoryApi(
                                Arrays.asList("invalid", "path", "directory"),
                                Arrays.asList("invalid", "path", "directory"),
                                new ArrayList<>(),
                                new ArrayList<>(),
                                "testFolder",
                                "testFolder",
                                1
                        )))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void moveDirectory_fail_invalidToPathDirectory() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/directory/move")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(new DataMoveDirectoryApi(
                                new ArrayList<>(),
                                new ArrayList<>(),
                                Arrays.asList("invalid", "path", "directory"),
                                Arrays.asList("invalid", "path", "directory"),
                                "testFolder",
                                "testFolder",
                                1
                        )))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void moveDirectory_fail_forbiddenSourceDirectory() throws Exception {
        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                "ROLE_USER",
                "testDirectory",
                "testDirectory",
                "testDirectory",
                new ArrayList<>());
        directoryRedisRepository.save(directoryRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/directory/move")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(
                                new DataMoveDirectoryApi(
                                        List.of("testDirectory"),
                                        List.of("testDirectory"),
                                        new ArrayList<>(),
                                        new ArrayList<>(),
                                        "testFolder",
                                        "testFolder",
                                        1
                                )))
                )
                .andExpect(status().isForbidden());

        directoryRedisRepository.delete(directoryRedis);
    }

    @Test
    public void moveDirectory_fail_forbiddenTargetDirectory() throws Exception {
        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                "ROLE_USER",
                "testDirectory",
                "testDirectory",
                "testDirectory",
                new ArrayList<>());
        directoryRedisRepository.save(directoryRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/directory/move")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(
                                new DataMoveDirectoryApi(
                                        new ArrayList<>(),
                                        new ArrayList<>(),
                                        List.of("testDirectory"),
                                        List.of("testDirectory"),
                                        "testFolder",
                                        "testFolder",
                                        1
                                )))
                )
                .andExpect(status().isForbidden());

        directoryRedisRepository.delete(directoryRedis);
    }

    @Test
    public void moveDirectory_success_forbidden() throws Exception {
        final String testDirectorySource = UUID.randomUUID().toString();
        final String testDirectoryTarget = UUID.randomUUID().toString();
        final String testFolder = UUID.randomUUID().toString();
        final String testFileName = UUID.randomUUID().toString();
        final Path testDirectorySourcePath = Path.of(rootPath.getRootPath(), testDirectorySource);
        final Path testDirectoryTargetPath = Path.of(rootPath.getRootPath(), testDirectoryTarget);
        final Path testFolderPath = Path.of(testDirectorySourcePath.toString(), testFolder);
        final Path testFile = Path.of(testFolderPath.toString(), testFileName);

        Files.createDirectory(testDirectorySourcePath);
        Files.createDirectory(testDirectoryTargetPath);
        Files.createDirectory(testFolderPath);
        Files.createFile(testFile);

        DirectoryRedis directoryRedisSource = new DirectoryRedis(
                "login",
                "ROLE_USER",
                testDirectorySource,
                testDirectorySource,
                testDirectorySourcePath.toString(),
                List.of(UtilsAuthAction.LOGIN));
        DirectoryRedis directoryRedisTarget = new DirectoryRedis(
                "login",
                "ROLE_USER",
                testDirectoryTarget,
                testDirectoryTarget,
                testDirectoryTargetPath.toString(),
                List.of(UtilsAuthAction.LOGIN));
        DirectoryRedis directoryRedisFolder = new DirectoryRedis(
                UtilsAuthAction.LOGIN,
                "ROLE_USER",
                testFolder,
                testFolder,
                testFolderPath.toString(),
                new ArrayList<>());
        FileRedis fileRedis = new FileRedis(
                UtilsAuthAction.LOGIN,
                "ROLE_USER",
                testFileName,
                testFileName,
                testFile.toString(),
                new ArrayList<>());
        RightUserFileSystemObjectRedis rightDirectorySource = new RightUserFileSystemObjectRedis(
                testDirectorySource + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.SHOW));
        RightUserFileSystemObjectRedis rightDirectoryTarget = new RightUserFileSystemObjectRedis(
                testDirectoryTarget + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.SHOW, OperationRights.COPY));
        directoryRedisRepository.saveAll(List.of(directoryRedisSource, directoryRedisFolder,
                directoryRedisTarget));
        fileRedisRepository.save(fileRedis);
        rightUserFileSystemObjectRedisRepository.saveAll(List.of(rightDirectorySource, rightDirectoryTarget));

        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/directory/move")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(
                                new DataMoveDirectoryApi(
                                        List.of(testDirectorySource),
                                        List.of(testDirectorySource),
                                        List.of(testDirectoryTarget),
                                        List.of(testDirectoryTarget),
                                        testFolder,
                                        testFolder,
                                        2
                                )))
                )
                .andExpect(status().isNoContent());

        directoryRedisRepository.deleteAll(List.of(directoryRedisSource, directoryRedisTarget));
        fileRedisRepository.delete(fileRedis);
        rightUserFileSystemObjectRedisRepository.deleteAll(List.of(rightDirectorySource, rightDirectoryTarget));

        FileSystemUtils.deleteRecursively(testDirectorySourcePath);
        FileSystemUtils.deleteRecursively(testDirectoryTargetPath);
    }

    @Test
    public void moveDirectory_fail_isFrozenDirectorySource() throws Exception {
        final String testDirectorySource = UUID.randomUUID().toString();
        final String testDirectoryTarget = UUID.randomUUID().toString();

        DirectoryRedis directoryRedisSource = new DirectoryRedis(
                "login",
                "ROLE_USER",
                testDirectorySource,
                testDirectorySource,
                testDirectorySource,
                List.of(UtilsAuthAction.LOGIN));
        DirectoryRedis directoryRedisTarget = new DirectoryRedis(
                UtilsAuthAction.LOGIN,
                "ROLE_USER",
                testDirectoryTarget,
                testDirectoryTarget,
                testDirectoryTarget,
                new ArrayList<>());
        RightUserFileSystemObjectRedis rightDirectorySource = new RightUserFileSystemObjectRedis(
                testDirectorySource + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.SHOW));
        FrozenFileSystemObjectRedis frozenFileSystemObjectRedis = new FrozenFileSystemObjectRedis(
                testDirectorySource);

        directoryRedisRepository.saveAll(List.of(directoryRedisSource, directoryRedisTarget));
        frozenFileSystemObjectRedisRepository.save(frozenFileSystemObjectRedis);
        rightUserFileSystemObjectRedisRepository.save(rightDirectorySource);

        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/directory/move")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(
                                new DataMoveDirectoryApi(
                                        List.of(testDirectorySource),
                                        List.of(testDirectorySource),
                                        new ArrayList<>(),
                                        new ArrayList<>(),
                                        testDirectoryTarget,
                                        testDirectoryTarget,
                                        2
                                )))
                )
                .andExpect(status().isBadRequest());

        frozenFileSystemObjectRedisRepository.delete(frozenFileSystemObjectRedis);
        directoryRedisRepository.deleteAll(List.of(directoryRedisSource, directoryRedisTarget));
        rightUserFileSystemObjectRedisRepository.delete(rightDirectorySource);
    }

    @Test
    public void moveDirectory_fail_isFrozenDirectoryTarget() throws Exception {
        final String testDirectoryTarget = UUID.randomUUID().toString();

        DirectoryRedis directoryRedisSource = new DirectoryRedis(
                "login",
                "ROLE_USER",
                testDirectoryTarget,
                testDirectoryTarget,
                testDirectoryTarget,
                List.of(UtilsAuthAction.LOGIN));
        RightUserFileSystemObjectRedis rightDirectorySource = new RightUserFileSystemObjectRedis(
                testDirectoryTarget + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.SHOW));
        FrozenFileSystemObjectRedis frozenFileSystemObjectRedis = new FrozenFileSystemObjectRedis(
                testDirectoryTarget);

        directoryRedisRepository.save(directoryRedisSource);
        frozenFileSystemObjectRedisRepository.save(frozenFileSystemObjectRedis);
        rightUserFileSystemObjectRedisRepository.save(rightDirectorySource);

        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/directory/move")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(
                                new DataMoveDirectoryApi(
                                        new ArrayList<>(),
                                        new ArrayList<>(),
                                        List.of(testDirectoryTarget),
                                        List.of(testDirectoryTarget),
                                        "folder",
                                        "folder",
                                        2
                                )))
                )
                .andExpect(status().isBadRequest());

        frozenFileSystemObjectRedisRepository.delete(frozenFileSystemObjectRedis);
        directoryRedisRepository.delete(directoryRedisSource);
        rightUserFileSystemObjectRedisRepository.delete(rightDirectorySource);
    }

}
