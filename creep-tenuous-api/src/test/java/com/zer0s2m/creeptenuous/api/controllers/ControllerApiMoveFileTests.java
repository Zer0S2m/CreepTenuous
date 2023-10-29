package com.zer0s2m.creeptenuous.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zer0s2m.creeptenuous.api.helpers.UtilsActionForFiles;
import com.zer0s2m.creeptenuous.common.components.RootPath;
import com.zer0s2m.creeptenuous.common.data.DataMoveFileApi;
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
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.FileSystemUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagControllerApi
public class ControllerApiMoveFileTests {

    Logger logger = LogManager.getLogger(ControllerApiMoveFileTests.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final ServiceBuildDirectoryPath buildDirectoryPath = new ServiceBuildDirectoryPath();

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

    private final String testFile1 = "testFile1.txt";

    private final String testFile2 = "testFile2.txt";

    private final String testFile3 = "testFile3.txt";

    DataMoveFileApi RECORD_1 = new DataMoveFileApi(
            testFile1,
            testFile1,
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            List.of("testFolder1"),
            List.of("testFolder1")
    );

    DataMoveFileApi RECORD_2 = new DataMoveFileApi(
            null,
            null,
            Arrays.asList(testFile2, testFile3),
            Arrays.asList(testFile2, testFile3),
            new ArrayList<>(),
            new ArrayList<>(),
            List.of("testFolder2"),
            List.of("testFolder2")
    );

    @Test
    public void moveOneFile_success() throws Exception {
        Path pathTestFile = UtilsActionForFiles.preparePreliminaryFiles(
                RECORD_1.fileName(), RECORD_1.parents(), logger, buildDirectoryPath
        );
        Path pathTestFolder = Paths.get(
                rootPath.getRootPath(), RECORD_1.toParents().get(0)
        );
        Files.createFile(pathTestFile);
        Files.createDirectory(pathTestFolder);
        logger.info("Create folder for tests: " + pathTestFolder);

        Assertions.assertTrue(Files.exists(pathTestFile));
        Assertions.assertTrue(Files.exists(pathTestFolder));

        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/file/move")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(RECORD_1))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNoContent());

        Path newPathTestFile = UtilsActionForFiles.preparePreliminaryFiles(
                RECORD_1.fileName(), RECORD_1.toParents(), logger, buildDirectoryPath
        );

        Assertions.assertFalse(Files.exists(pathTestFile));
        Assertions.assertTrue(Files.exists(newPathTestFile));

        UtilsActionForFiles.deleteFileAndWriteLog(newPathTestFile, logger);
        UtilsActionForFiles.deleteFileAndWriteLog(pathTestFolder, logger);
    }

    @Test
    public void moveMoreOneFile_success() throws Exception {
        List<String> nameFiles = RECORD_2.nameFiles();

        assert nameFiles != null;
        Path pathTestFile1 = UtilsActionForFiles.preparePreliminaryFiles(
                nameFiles.get(0), RECORD_2.parents(), logger, buildDirectoryPath
        );
        Path pathTestFile2 = UtilsActionForFiles.preparePreliminaryFiles(
                nameFiles.get(1), RECORD_2.parents(), logger, buildDirectoryPath
        );
        Path pathTestFolder = Paths.get(
                rootPath.getRootPath(), RECORD_2.toParents().get(0)
        );
        Files.createFile(pathTestFile1);
        Files.createFile(pathTestFile2);
        Files.createDirectory(pathTestFolder);

        logger.info("Create folder for tests: " + pathTestFolder);

        Assertions.assertTrue(Files.exists(pathTestFile1));
        Assertions.assertTrue(Files.exists(pathTestFile2));
        Assertions.assertTrue(Files.exists(pathTestFolder));

        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/file/move")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(RECORD_2))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNoContent());

        Path newPathTestFile1 = UtilsActionForFiles.preparePreliminaryFiles(
                nameFiles.get(0), RECORD_2.toParents(), logger, buildDirectoryPath
        );
        Path newPathTestFile2 = UtilsActionForFiles.preparePreliminaryFiles(
                nameFiles.get(1), RECORD_2.toParents(), logger, buildDirectoryPath
        );

        Assertions.assertFalse(Files.exists(pathTestFile1));
        Assertions.assertFalse(Files.exists(pathTestFile2));
        Assertions.assertTrue(Files.exists(newPathTestFile1));
        Assertions.assertTrue(Files.exists(newPathTestFile2));

        UtilsActionForFiles.deleteFileAndWriteLog(newPathTestFile1, logger);
        UtilsActionForFiles.deleteFileAndWriteLog(newPathTestFile2, logger);
        UtilsActionForFiles.deleteFileAndWriteLog(pathTestFolder, logger);
    }

    @Test
    @DisplayName("Failed moving file")
    public void moveFile_fail_notValidParents() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/file/move")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(
                                new DataMoveFileApi(
                                        "testFile.txt",
                                        "testFile.txt",
                                        null,
                                        null,
                                        new ArrayList<>(),
                                        new ArrayList<>(),
                                        Arrays.asList("not", "valid", "directory"),
                                        Arrays.asList("not", "valid", "directory")
                                )
                        ))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Failed moving multiple files")
    public void moveFiles_fail_fileNotIsExists() throws Exception {
        MockHttpServletRequestBuilder builderRequest = MockMvcRequestBuilders.put("/api/v1/file/move")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", accessToken);

        this.mockMvc.perform(builderRequest
                .content(objectMapper.writeValueAsString(
                        new DataMoveFileApi(
                                "fileNotIsExists.txt",
                                "fileNotIsExists.txt",
                                null,
                                null,
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new ArrayList<>()
                        )
                )))
                .andExpect(status().isNotFound());
        this.mockMvc.perform(builderRequest
                .content(objectMapper.writeValueAsString(
                        new DataMoveFileApi(
                                null,
                                null,
                                Arrays.asList("fileNotIsExists1.txt", "fileNotIsExists2.txt"),
                                Arrays.asList("fileNotIsExists1.txt", "fileNotIsExists2.txt"),
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new ArrayList<>()
                        )
                )))
                .andExpect(status().isNotFound());
    }

    @Test
    public void moveOneFile_fail_notValidParents() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/file/move")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(
                                new DataMoveFileApi(
                                        "file.txt",
                                        "file.txt",
                                        null,
                                        null,
                                        null,
                                        null,
                                        new ArrayList<>(),
                                        new ArrayList<>()
                                )
                        ))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void moveOneFile_fail_notValidToParents() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/file/move")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(
                                new DataMoveFileApi(
                                        "file.txt",
                                        "file.txt",
                                        null,
                                        null,
                                        new ArrayList<>(),
                                        new ArrayList<>(),
                                        null,
                                        null
                                )
                        ))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void moveOneFile_fail_forbiddenDirectories() throws Exception {
        DataMoveFileApi dataMoveFileApi = new DataMoveFileApi(
                testFile1,
                testFile1,
                new ArrayList<>(),
                new ArrayList<>(),
                List.of("testDirectoryFrom"),
                List.of("testDirectoryFrom"),
                List.of("testFolder1"),
                List.of("testFolder1")
        );

        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                "ROLE_USER",
                "testDirectoryFrom",
                "testDirectoryFrom",
                "testDirectoryFrom",
                new ArrayList<>());
        directoryRedisRepository.save(directoryRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/file/move")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(dataMoveFileApi))
                )
                .andExpect(status().isForbidden());

        directoryRedisRepository.delete(directoryRedis);
    }

    @Test
    public void moveOneFile_fail_forbiddenFile() throws Exception {
        DataMoveFileApi dataMoveFileApi = new DataMoveFileApi(
                testFile1,
                testFile1,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        FileRedis fileRedis = new FileRedis(
                "login",
                "ROLE_USER",
                testFile1,
                testFile1,
                testFile1,
                new ArrayList<>());
        fileRedisRepository.save(fileRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/file/move")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(dataMoveFileApi))
                )
                .andExpect(status().isForbidden());

        fileRedisRepository.delete(fileRedis);
    }

    @Test
    public void moveMoreOneFile_fail_forbiddenFile() throws Exception {
        DataMoveFileApi dataMoveFileApi = new DataMoveFileApi(
                null,
                null,
                List.of(testFile1),
                List.of(testFile1),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        FileRedis fileRedis = new FileRedis(
                "login",
                "ROLE_USER",
                testFile1,
                testFile1,
                testFile1,
                new ArrayList<>());
        fileRedisRepository.save(fileRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/file/move")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(dataMoveFileApi))
                )
                .andExpect(status().isForbidden());

        fileRedisRepository.delete(fileRedis);
    }

    @Test
    public void moveOneFile_success_forbidden() throws Exception {
        final String testDirectory = UUID.randomUUID().toString();
        final Path testDirectoryPath = Path.of(rootPath.getRootPath(), testDirectory);
        final Path testFilePath = Path.of(rootPath.getRootPath(), testFile1);

        Files.createDirectory(testDirectoryPath);
        Files.createFile(testFilePath);

        prepareCopy(testDirectory, testFilePath, testDirectoryPath);

        DataMoveFileApi dataMoveFileApi = new DataMoveFileApi(
                testFile1,
                testFile1,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                List.of(testDirectory),
                List.of(testDirectory)
        );

        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/file/move")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(dataMoveFileApi)
                ))
                .andExpect(status().isNoContent());

        fileRedisRepository.deleteById(testFile1);
        directoryRedisRepository.deleteById(testDirectory);
        rightUserFileSystemObjectRedisRepository.deleteAllById(List.of(
                testDirectory + "__" + UtilsAuthAction.LOGIN, testFile1 + "__" + UtilsAuthAction.LOGIN));

        Files.deleteIfExists(testFilePath);
        FileSystemUtils.deleteRecursively(testDirectoryPath);
    }

    @Test
    public void moveOneFile_fail_isFrozenFile() throws Exception {
        final String testDirectory = "testDirectory";
        final Path testDirectoryPath = Path.of(rootPath.getRootPath(), testDirectory);
        final Path testFilePath = Path.of(rootPath.getRootPath(), testFile1);

        prepareCopy(testDirectory, testFilePath, testDirectoryPath);

        DataMoveFileApi dataMoveFileApi = new DataMoveFileApi(
                testFile1,
                testFile1,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                List.of(testDirectory),
                List.of(testDirectory)
        );
        FrozenFileSystemObjectRedis frozenFileSystemObjectRedis = new FrozenFileSystemObjectRedis(
                testFile1);

        frozenFileSystemObjectRedisRepository.save(frozenFileSystemObjectRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/file/move")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(dataMoveFileApi)
                        ))
                .andExpect(status().isBadRequest());

        fileRedisRepository.deleteById(testFile1);
        directoryRedisRepository.deleteById(testDirectory);
        frozenFileSystemObjectRedisRepository.delete(frozenFileSystemObjectRedis);
        rightUserFileSystemObjectRedisRepository.deleteAllById(List.of(
                testDirectory + "__" + UtilsAuthAction.LOGIN, testFile1 + "__" + UtilsAuthAction.LOGIN));
    }

    @Test
    public void moveOneFile_fail_isFrozenDirectories() throws Exception {
        final String testDirectory = "testDirectory";
        final Path testDirectoryPath = Path.of(rootPath.getRootPath(), testDirectory);
        final Path testFilePath = Path.of(rootPath.getRootPath(), testFile1);

        prepareCopy(testDirectory, testFilePath, testDirectoryPath);

        DataMoveFileApi dataMoveFileApi = new DataMoveFileApi(
                testFile1,
                testFile1,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                List.of(testDirectory),
                List.of(testDirectory)
        );
        FrozenFileSystemObjectRedis frozenFileSystemObjectRedis = new FrozenFileSystemObjectRedis(
                testDirectory);

        frozenFileSystemObjectRedisRepository.save(frozenFileSystemObjectRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/file/move")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(dataMoveFileApi)
                        ))
                .andExpect(status().isBadRequest());

        fileRedisRepository.deleteById(testFile1);
        directoryRedisRepository.deleteById(testDirectory);
        frozenFileSystemObjectRedisRepository.delete(frozenFileSystemObjectRedis);
        rightUserFileSystemObjectRedisRepository.deleteAllById(List.of(
                testDirectory + "__" + UtilsAuthAction.LOGIN, testFile1 + "__" + UtilsAuthAction.LOGIN));
    }

    @Test
    public void moveMoreOneFile_success_forbidden() throws Exception {
        final String testDirectory = UUID.randomUUID().toString();
        final Path testDirectoryPath = Path.of(rootPath.getRootPath(), testDirectory);
        final Path testFilePath = Path.of(rootPath.getRootPath(), testFile1);

        Files.createDirectory(testDirectoryPath);
        Files.createFile(testFilePath);

        prepareCopy(testDirectory, testFilePath, testDirectoryPath);

        DataMoveFileApi dataMoveFileApi = new DataMoveFileApi(
                null,
                null,
                List.of(testFile1),
                List.of(testFile1),
                new ArrayList<>(),
                new ArrayList<>(),
                List.of(testDirectory),
                List.of(testDirectory)
        );

        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/file/move")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(dataMoveFileApi)
                        ))
                .andExpect(status().isNoContent());

        fileRedisRepository.deleteById(testFile1);
        directoryRedisRepository.deleteById(testDirectory);
        rightUserFileSystemObjectRedisRepository.deleteAllById(List.of(
                testDirectory + "__" + UtilsAuthAction.LOGIN, testFile1 + "__" + UtilsAuthAction.LOGIN));

        Files.deleteIfExists(testFilePath);
        FileSystemUtils.deleteRecursively(testDirectoryPath);
    }

    @Test
    public void moveMoreOneFile_fail_isFrozenFile() throws Exception {
        final String testDirectory = "testDirectory";
        final Path testDirectoryPath = Path.of(rootPath.getRootPath(), testDirectory);
        final Path testFilePath = Path.of(rootPath.getRootPath(), testFile1);

        prepareCopy(testDirectory, testFilePath, testDirectoryPath);

        DataMoveFileApi dataMoveFileApi = new DataMoveFileApi(
                null,
                null,
                List.of(testFile1),
                List.of(testFile1),
                new ArrayList<>(),
                new ArrayList<>(),
                List.of(testDirectory),
                List.of(testDirectory)
        );
        FrozenFileSystemObjectRedis frozenFileSystemObjectRedis = new FrozenFileSystemObjectRedis(
                testFile1);

        frozenFileSystemObjectRedisRepository.save(frozenFileSystemObjectRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/file/move")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(dataMoveFileApi)
                        ))
                .andExpect(status().isBadRequest());

        fileRedisRepository.deleteById(testFile1);
        directoryRedisRepository.deleteById(testDirectory);
        frozenFileSystemObjectRedisRepository.delete(frozenFileSystemObjectRedis);
        rightUserFileSystemObjectRedisRepository.deleteAllById(List.of(
                testDirectory + "__" + UtilsAuthAction.LOGIN, testFile1 + "__" + UtilsAuthAction.LOGIN));
    }

    private void prepareCopy(String testDirectory, Path testFilePath, Path testDirectoryPath) {
        FileRedis fileRedis = new FileRedis(
                "login",
                "ROLE_USER",
                testFile1,
                testFile1,
                testFilePath.toString(),
                List.of(UtilsAuthAction.LOGIN));
        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                "ROLE_USER",
                testDirectory,
                testDirectory,
                testDirectoryPath.toString(),
                List.of(UtilsAuthAction.LOGIN));
        RightUserFileSystemObjectRedis rightDirectory = new RightUserFileSystemObjectRedis(
                testDirectory + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.SHOW, OperationRights.MOVE));
        RightUserFileSystemObjectRedis rightFile = new RightUserFileSystemObjectRedis(
                testFile1 + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.SHOW, OperationRights.MOVE));
        fileRedisRepository.save(fileRedis);
        directoryRedisRepository.save(directoryRedis);
        rightUserFileSystemObjectRedisRepository.saveAll(List.of(rightDirectory, rightFile));
    }

}
