package com.zer0s2m.creeptenuous.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zer0s2m.creeptenuous.api.helpers.UtilsActionForFiles;
import com.zer0s2m.creeptenuous.common.components.RootPath;
import com.zer0s2m.creeptenuous.common.data.DataCopyFileApi;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.models.RightUserFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagControllerApi
public class ControllerApiCopyFileTests {

    Logger logger = LogManager.getLogger(ControllerApiCopyFileTests.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RootPath rootPath;

    @Autowired
    private ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    private DirectoryRedisRepository directoryRedisRepository;

    @Autowired
    private FileRedisRepository fileRedisRepository;

    @Autowired
    private RightUserFileSystemObjectRedisRepository rightUserFileSystemObjectRedisRepository;

    private final String accessToken = UtilsAuthAction.builderHeader(UtilsAuthAction.generateAccessToken());

    private final String testFile1 = "testFile1.txt";
    private final String testFile2 = "testFile2.txt";
    private final String testFile3 = "testFile3.txt";

    DataCopyFileApi RECORD_1 = new DataCopyFileApi(
            testFile1,
            testFile1,
            null,
            null,
            new ArrayList<>(),
            new ArrayList<>(),
            List.of("testFolder1"),
            List.of("testFolder1")
    );

    DataCopyFileApi RECORD_2 = new DataCopyFileApi(
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
    public void copyOneFile_success() throws Exception {
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
                MockMvcRequestBuilders.post("/api/v1/file/copy")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(RECORD_1))
                        .header("Authorization",  accessToken)
                )
                .andExpect(status().isCreated());

        UtilsActionForFiles.deleteFileAndWriteLog(pathTestFile, logger);
        FileSystemUtils.deleteRecursively(pathTestFolder);
    }

    @Test
    public void copyMoreOneFile_success() throws Exception {
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
                MockMvcRequestBuilders.post("/api/v1/file/copy")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(RECORD_2))
                        .header("Authorization",  accessToken)
                )
                .andExpect(status().isCreated());

        UtilsActionForFiles.preparePreliminaryFiles(
                nameFiles.get(0), RECORD_2.toParents(), logger, buildDirectoryPath
        );
        UtilsActionForFiles.preparePreliminaryFiles(
                nameFiles.get(1), RECORD_2.toParents(), logger, buildDirectoryPath
        );

        Assertions.assertTrue(Files.exists(pathTestFile1));
        Assertions.assertTrue(Files.exists(pathTestFile2));

        UtilsActionForFiles.deleteFileAndWriteLog(pathTestFile1, logger);
        UtilsActionForFiles.deleteFileAndWriteLog(pathTestFile2, logger);
        FileSystemUtils.deleteRecursively(pathTestFolder);
    }

    @Test
    public void copyFile_fail_notValidParents() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/file/copy")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization",  accessToken)
                        .content(objectMapper.writeValueAsString(
                                new DataCopyFileApi(
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
    public void copyFile_fail_notValidToParents() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/file/copy")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization",  accessToken)
                        .content(objectMapper.writeValueAsString(
                                new DataCopyFileApi(
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
    public void copyOneFile_fail_forbiddenSourceDirectory() throws Exception {
        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                "ROLE_USER",
                "testDirectoryFrom",
                "testDirectoryFrom",
                "testDirectoryFrom",
                new ArrayList<>());
        directoryRedisRepository.save(directoryRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/file/copy")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization",  accessToken)
                        .content(objectMapper.writeValueAsString(
                                new DataCopyFileApi(
                                        "file.txt",
                                        "file.txt",
                                        new ArrayList<>(),
                                        new ArrayList<>(),
                                        List.of("testDirectoryFrom"),
                                        List.of("testDirectoryFrom"),
                                        new ArrayList<>(),
                                        new ArrayList<>()
                                )
                        ))
                )
                .andExpect(status().isForbidden());

        directoryRedisRepository.delete(directoryRedis);
    }

    @Test
    public void copyOneFile_fail_forbiddenTargetDirectory() throws Exception {
        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                "ROLE_USER",
                "testDirectoryTo",
                "testDirectoryTo",
                "testDirectoryTo",
                new ArrayList<>());
        directoryRedisRepository.save(directoryRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/file/copy")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization",  accessToken)
                        .content(objectMapper.writeValueAsString(
                                new DataCopyFileApi(
                                        "file.txt",
                                        "file.txt",
                                        new ArrayList<>(),
                                        new ArrayList<>(),
                                        new ArrayList<>(),
                                        new ArrayList<>(),
                                        List.of("testDirectoryTo"),
                                        List.of("testDirectoryTo")
                                )
                        ))
                )
                .andExpect(status().isForbidden());

        directoryRedisRepository.delete(directoryRedis);
    }

    @Test
    public void copyOneFile_fail_forbiddenFile() throws Exception {
        FileRedis fileRedis = new FileRedis(
                "login",
                "ROLE_USER",
                "testFile.txt",
                "testFile.txt",
                "testFile.txt",
                new ArrayList<>());
        fileRedisRepository.save(fileRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/file/copy")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization",  accessToken)
                        .content(objectMapper.writeValueAsString(
                                new DataCopyFileApi(
                                        "testFile.txt",
                                        "testFile.txt",
                                        new ArrayList<>(),
                                        new ArrayList<>(),
                                        new ArrayList<>(),
                                        new ArrayList<>(),
                                        new ArrayList<>(),
                                        new ArrayList<>()
                                )
                        ))
                )
                .andExpect(status().isForbidden());

        fileRedisRepository.delete(fileRedis);
    }

    @Test
    public void copyMoreOneFile_fail_forbiddenFile() throws Exception {
        FileRedis fileRedis = new FileRedis(
                "login",
                "ROLE_USER",
                "testFile.txt",
                "testFile.txt",
                "testFile.txt",
                new ArrayList<>());
        fileRedisRepository.save(fileRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/file/copy")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization",  accessToken)
                        .content(objectMapper.writeValueAsString(
                                new DataCopyFileApi(
                                        null,
                                        null,
                                        List.of("testFile.txt"),
                                        List.of("testFile.txt"),
                                        new ArrayList<>(),
                                        new ArrayList<>(),
                                        new ArrayList<>(),
                                        new ArrayList<>()
                                        )
                                ))
                )
                .andExpect(status().isForbidden());

        fileRedisRepository.delete(fileRedis);
    }

    @Test
    public void copyOneFile_success_forbidden() throws Exception {
        final String fileName = "testFile.txt";
        final String directoryName = "testDirectory";
        final Path directoryPath = Path.of(rootPath.getRootPath(), directoryName);
        final Path filePath = Path.of(rootPath.getRootPath(), fileName);
        Files.createDirectory(directoryPath);
        Files.createFile(filePath);

        prepareCopy(fileName, directoryName);

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/file/copy")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization",  accessToken)
                        .content(objectMapper.writeValueAsString(
                                new DataCopyFileApi(
                                        fileName,
                                        fileName,
                                        new ArrayList<>(),
                                        new ArrayList<>(),
                                        new ArrayList<>(),
                                        new ArrayList<>(),
                                        List.of(directoryName),
                                        List.of(directoryName)
                                )
                        ))
                )
                .andExpect(status().isCreated());

        fileRedisRepository.deleteById(fileName);
        directoryRedisRepository.deleteById(directoryName);
        rightUserFileSystemObjectRedisRepository.deleteAllById(List.of(
                fileName + "__" + UtilsAuthAction.LOGIN, directoryName + "__" + UtilsAuthAction.LOGIN));

        FileSystemUtils.deleteRecursively(directoryPath);
        Files.deleteIfExists(filePath);
    }

    @Test
    public void copyMoreOneFile_success_forbidden() throws Exception {
        final String fileName = "testFile.txt";
        final String directoryName = "testDirectory";
        final Path directoryPath = Path.of(rootPath.getRootPath(), directoryName);
        final Path filePath = Path.of(rootPath.getRootPath(), fileName);
        Files.createDirectory(directoryPath);
        Files.createFile(filePath);

        prepareCopy(fileName, directoryName);

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/file/copy")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization",  accessToken)
                        .content(objectMapper.writeValueAsString(
                                new DataCopyFileApi(
                                        null,
                                        null,
                                        List.of(fileName),
                                        List.of(fileName),
                                        new ArrayList<>(),
                                        new ArrayList<>(),
                                        List.of(directoryName),
                                        List.of(directoryName)
                                )
                        ))
                )
                .andExpect(status().isCreated());

        fileRedisRepository.deleteById(fileName);
        directoryRedisRepository.deleteById(directoryName);
        rightUserFileSystemObjectRedisRepository.deleteAllById(List.of(
                fileName + "__" + UtilsAuthAction.LOGIN, directoryName + "__" + UtilsAuthAction.LOGIN));

        FileSystemUtils.deleteRecursively(directoryPath);
        Files.deleteIfExists(filePath);
    }

    private void prepareCopy(String fileName, String directoryName) {
        FileRedis fileRedis = new FileRedis(
                "login",
                "ROLE_USER",
                fileName,
                fileName,
                fileName,
                List.of(UtilsAuthAction.LOGIN));
        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                "ROLE_USER",
                directoryName,
                directoryName,
                directoryName,
                List.of(UtilsAuthAction.LOGIN));
        RightUserFileSystemObjectRedis rightDirectory = new RightUserFileSystemObjectRedis(
                directoryName + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.SHOW, OperationRights.COPY));
        RightUserFileSystemObjectRedis rightFile = new RightUserFileSystemObjectRedis(
                fileName + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.SHOW, OperationRights.COPY));
        fileRedisRepository.save(fileRedis);
        directoryRedisRepository.save(directoryRedis);
        rightUserFileSystemObjectRedisRepository.saveAll(List.of(rightDirectory, rightFile));
    }

}
