package com.zer0s2m.creeptenuous.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zer0s2m.creeptenuous.api.helpers.UtilsActionForFiles;
import com.zer0s2m.creeptenuous.common.components.RootPath;
import com.zer0s2m.creeptenuous.common.data.DataCopyDirectoryApi;
import com.zer0s2m.creeptenuous.common.enums.MethodCopyDirectory;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagControllerApi
public class ControllerApiCopyDirectoryTests {

    Logger logger = LogManager.getLogger(ControllerApiCreateDirectoryTests.class);

    @Autowired
    private ServiceBuildDirectoryPath serviceBuildDirectoryPath;

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
    public void copyDirectoryFolder_success() throws Exception {
        UtilsActionForFiles.createDirectories(DIRECTORIES_3, serviceBuildDirectoryPath, logger);
        UtilsActionForFiles.preparePreliminaryFilesForCopyDirectories(
                serviceBuildDirectoryPath,
                logger,
                DIRECTORIES_1,
                "test_file1.txt"
        );
        UtilsActionForFiles.preparePreliminaryFilesForCopyDirectories(
                serviceBuildDirectoryPath,
                logger,
                DIRECTORIES_2,
                "test_file2.txt"
        );

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/copy")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(new DataCopyDirectoryApi(
                                new ArrayList<>(),
                                new ArrayList<>(),
                                List.of(DIRECTORIES_3.get(0)),
                                List.of(DIRECTORIES_3.get(0)),
                                DIRECTORIES_1.get(0),
                                DIRECTORIES_1.get(0),
                                MethodCopyDirectory.FOLDER.getMethod()
                        )))
                )
                .andExpect(status().isCreated());

        FileSystemUtils.deleteRecursively(Path.of(serviceBuildDirectoryPath.build(DIRECTORIES_1)));
        FileSystemUtils.deleteRecursively(Path.of(serviceBuildDirectoryPath.build(DIRECTORIES_3)));
    }

    @Test
    public void copyDirectoryContent_success() throws Exception {
        UtilsActionForFiles.createDirectories(DIRECTORIES_3, serviceBuildDirectoryPath, logger);
        UtilsActionForFiles.preparePreliminaryFilesForCopyDirectories(
                serviceBuildDirectoryPath,
                logger,
                DIRECTORIES_1,
                "test_file1.txt"
        );
        UtilsActionForFiles.preparePreliminaryFilesForCopyDirectories(
                serviceBuildDirectoryPath,
                logger,
                DIRECTORIES_2,
                "test_file2.txt"
        );

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/copy")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(new DataCopyDirectoryApi(
                                new ArrayList<>(),
                                new ArrayList<>(),
                                List.of(DIRECTORIES_3.get(0)),
                                List.of(DIRECTORIES_3.get(0)),
                                DIRECTORIES_1.get(0),
                                DIRECTORIES_1.get(0),
                                MethodCopyDirectory.CONTENT.getMethod()
                        )))
                )
                .andExpect(status().isCreated());

        FileSystemUtils.deleteRecursively(Path.of(serviceBuildDirectoryPath.build(DIRECTORIES_1)));
        FileSystemUtils.deleteRecursively(Path.of(serviceBuildDirectoryPath.build(DIRECTORIES_3)));
    }

    @Test
    public void copyDirectory_fail_invalidPathDirectory() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/copy")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(new DataCopyDirectoryApi(
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
    public void copyDirectory_fail_invalidToPathDirectory() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/copy")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(new DataCopyDirectoryApi(
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
    public void copyDirectory_fail_notParents() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/copy")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(new DataCopyDirectoryApi(
                                null,
                                null,
                                new ArrayList<>(),
                                new ArrayList<>(),
                                "testFolder",
                                "testFolder",
                                1
                        )))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void copyDirectory_fail_notToParents() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/copy")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(new DataCopyDirectoryApi(
                                new ArrayList<>(),
                                new ArrayList<>(),
                                null,
                                null,
                                "testFolder",
                                "testFolder",
                                1
                        )))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void copyDirectory_fail_notFileName() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/copy")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(new DataCopyDirectoryApi(
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new ArrayList<>(),
                                "",
                                "",
                                1
                        )))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void copyDirectory_fail_forbiddenSourceDirectory() throws Exception {
        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                "ROLE_USER",
                "testDirectoryFrom",
                "testDirectoryFrom",
                "testDirectoryFrom",
                new ArrayList<>());
        directoryRedisRepository.save(directoryRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/copy")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(new DataCopyDirectoryApi(
                                List.of("testDirectoryFrom"),
                                List.of("testDirectoryFrom"),
                                new ArrayList<>(),
                                new ArrayList<>(),
                                "test",
                                "test",
                                1
                        )))
                )
                .andExpect(status().isForbidden());

        directoryRedisRepository.delete(directoryRedis);
    }

    @Test
    public void copyDirectory_fail_forbiddenTargetDirectory() throws Exception {
        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                "ROLE_USER",
                "testDirectoryTo",
                "testDirectoryTo",
                "testDirectoryTo",
                new ArrayList<>());
        directoryRedisRepository.save(directoryRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/copy")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(new DataCopyDirectoryApi(
                                new ArrayList<>(),
                                new ArrayList<>(),
                                List.of("testDirectoryTo"),
                                List.of("testDirectoryTo"),
                                "test",
                                "test",
                                1
                        )))
                )
                .andExpect(status().isForbidden());

        directoryRedisRepository.delete(directoryRedis);
    }

    @Test
    public void copyDirectory_fail_forbiddenDirectory() throws Exception {
        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                "ROLE_USER",
                "testDirectory",
                "testDirectory",
                "testDirectory",
                new ArrayList<>());
        directoryRedisRepository.save(directoryRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/copy")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(new DataCopyDirectoryApi(
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new ArrayList<>(),
                                "testDirectory",
                                "testDirectory",
                                1
                        )))
                )
                .andExpect(status().isForbidden());

        directoryRedisRepository.delete(directoryRedis);
    }

    @Test
    public void copyDirectory_success_forbidden() throws Exception {
        final Path testDirectorySource = Path.of(rootPath.getRootPath(), "testDirectory1");
        final Path testDirectoryTarget = Path.of(rootPath.getRootPath(), "testDirectoryTarget");
        final Path testFileSource = Path.of(testDirectorySource.toString(), "testFile");
        Files.createDirectory(testDirectorySource);
        Files.createDirectory(testDirectoryTarget);
        Files.createFile(testFileSource);

        DirectoryRedis directoryRedisSource = new DirectoryRedis(
                "login",
                "ROLE_USER",
                "testDirectory1",
                "testDirectory1",
                testDirectorySource.toString(),
                List.of(UtilsAuthAction.LOGIN));
        DirectoryRedis directoryRedisTarget = new DirectoryRedis(
                "login",
                "ROLE_USER",
                "testDirectoryTarget",
                "testDirectoryTarget",
                testDirectoryTarget.toString(),
                List.of(UtilsAuthAction.LOGIN));
        FileRedis fileRedis = new FileRedis(
                "login",
                "ROLE_USER",
                "testFile",
                "testFile",
                testFileSource.toString(),
                List.of(UtilsAuthAction.LOGIN));
        RightUserFileSystemObjectRedis rightDirectorySource = new RightUserFileSystemObjectRedis(
                "testDirectory1" + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.SHOW, OperationRights.COPY));
        RightUserFileSystemObjectRedis rightDirectoryTarget = new RightUserFileSystemObjectRedis(
                "testDirectoryTarget" + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.SHOW, OperationRights.COPY));
        RightUserFileSystemObjectRedis rightFileSource = new RightUserFileSystemObjectRedis(
                "testFile" + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.SHOW, OperationRights.COPY));

        directoryRedisRepository.saveAll(List.of(directoryRedisSource, directoryRedisTarget));
        fileRedisRepository.save(fileRedis);
        rightUserFileSystemObjectRedisRepository.saveAll(List.of(rightDirectorySource, rightDirectoryTarget,
                rightFileSource));

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/copy")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(new DataCopyDirectoryApi(
                                new ArrayList<>(),
                                new ArrayList<>(),
                                List.of("testDirectoryTarget"),
                                List.of("testDirectoryTarget"),
                                "testDirectory1",
                                "testDirectory1",
                                1
                        )))
                )
                .andExpect(status().isCreated());

        directoryRedisRepository.deleteAll(List.of(directoryRedisSource, directoryRedisTarget));
        fileRedisRepository.delete(fileRedis);
        rightUserFileSystemObjectRedisRepository.deleteAll(List.of(rightDirectorySource, rightDirectoryTarget,
                rightFileSource));

        FileSystemUtils.deleteRecursively(testDirectorySource);
        FileSystemUtils.deleteRecursively(testDirectoryTarget);
    }

    @Test
    public void copyDirectory_fail_isFrozenDirectories() throws Exception {
        final Path testDirectorySource = Path.of(rootPath.getRootPath(), "testDirectory1");
        final Path testDirectoryTarget = Path.of(rootPath.getRootPath(), "testDirectoryTarget");
        final Path testFileSource = Path.of(testDirectorySource.toString(), "testFile");
        Files.createDirectory(testDirectorySource);
        Files.createDirectory(testDirectoryTarget);
        Files.createFile(testFileSource);

        DirectoryRedis directoryRedisTarget = new DirectoryRedis(
                "login",
                "ROLE_USER",
                "testDirectoryTarget",
                "testDirectoryTarget",
                testDirectoryTarget.toString(),
                List.of(UtilsAuthAction.LOGIN));
        RightUserFileSystemObjectRedis rightDirectoryTarget = new RightUserFileSystemObjectRedis(
                "testDirectoryTarget" + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.SHOW, OperationRights.COPY));
        FrozenFileSystemObjectRedis frozenFileSystemObjectRedis = new FrozenFileSystemObjectRedis(
                "testDirectoryTarget");

        directoryRedisRepository.save(directoryRedisTarget);
        frozenFileSystemObjectRedisRepository.save(frozenFileSystemObjectRedis);
        rightUserFileSystemObjectRedisRepository.save(rightDirectoryTarget);

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/copy")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(new DataCopyDirectoryApi(
                                new ArrayList<>(),
                                new ArrayList<>(),
                                List.of("testDirectoryTarget"),
                                List.of("testDirectoryTarget"),
                                "testDirectory1",
                                "testDirectory1",
                                1
                        )))
                )
                .andExpect(status().isBadRequest());

        directoryRedisRepository.delete(directoryRedisTarget);
        frozenFileSystemObjectRedisRepository.delete(frozenFileSystemObjectRedis);
        rightUserFileSystemObjectRedisRepository.delete(rightDirectoryTarget);

        FileSystemUtils.deleteRecursively(testDirectorySource);
        FileSystemUtils.deleteRecursively(testDirectoryTarget);
    }

}
