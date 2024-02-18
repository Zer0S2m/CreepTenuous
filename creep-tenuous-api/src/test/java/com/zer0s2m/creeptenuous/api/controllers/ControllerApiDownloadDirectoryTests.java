package com.zer0s2m.creeptenuous.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zer0s2m.creeptenuous.api.helpers.UtilsActionForFiles;
import com.zer0s2m.creeptenuous.common.components.RootPath;
import com.zer0s2m.creeptenuous.common.data.DataDownloadDirectoryApi;
import com.zer0s2m.creeptenuous.common.data.DataDownloadDirectorySelectApi;
import com.zer0s2m.creeptenuous.common.data.DataDownloadDirectorySelectPartApi;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.models.FrozenFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.models.RightUserFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FrozenFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.RightUserFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.core.services.ServiceBuildDirectoryPath;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagControllerApi
public class ControllerApiDownloadDirectoryTests {

    Logger logger = LogManager.getLogger(ControllerApiDownloadDirectoryTests.class);

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

    List<String> DIRECTORIES_1 = List.of("test_folder1");

    @Test
    public void downloadDirectory_success() throws Exception {
        UtilsActionForFiles.preparePreliminaryFilesForCopyDirectories(
                buildDirectoryPath,
                logger,
                DIRECTORIES_1,
                "test_file1.txt"
        );
        UtilsActionForFiles.preparePreliminaryFilesForCopyDirectories(
                buildDirectoryPath,
                logger,
                DIRECTORIES_1,
                "test_file2.txt"
        );

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/download")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataDownloadDirectoryApi(
                                new ArrayList<>(),
                                new ArrayList<>(),
                                DIRECTORIES_1.get(0),
                                DIRECTORIES_1.get(0)
                        )))
                        .header("Authorization", accessToken)
        ).andExpect(status().isOk());

        Path directoryTest = Path.of(buildDirectoryPath.build(DIRECTORIES_1));
        FileSystemUtils.deleteRecursively(directoryTest);

        logger.info("Delete folder for tests: " + directoryTest);
    }

    @Test
    public void downloadDirectorySelect_success() throws Exception {
        UtilsActionForFiles.preparePreliminaryFilesForCopyDirectories(
                buildDirectoryPath,
                logger,
                DIRECTORIES_1,
                "test_file1.txt"
        );
        UtilsActionForFiles.preparePreliminaryFilesForCopyDirectories(
                buildDirectoryPath,
                logger,
                DIRECTORIES_1,
                "test_file2.txt"
        );

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/download/select")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataDownloadDirectorySelectApi(
                                List.of(new DataDownloadDirectorySelectPartApi(
                                        new ArrayList<>(),
                                        new ArrayList<>(),
                                        DIRECTORIES_1.get(0),
                                        DIRECTORIES_1.get(0)
                                ))
                        )))
                        .header("Authorization", accessToken)
        ).andExpect(status().isOk());

        Path directoryTest = Path.of(buildDirectoryPath.build(DIRECTORIES_1));
        FileSystemUtils.deleteRecursively(directoryTest);

        logger.info("Delete folder for tests: " + directoryTest);
    }

    @Test
    public void downloadDirectory_fail_notValidNameDirectory() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/download")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataDownloadDirectoryApi(
                                new ArrayList<>(),
                                new ArrayList<>(),
                                null,
                                null
                        )))
                        .header("Authorization", accessToken)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void downloadDirectory_fail_notValidParents() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/download")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataDownloadDirectoryApi(
                                null,
                                null,
                                DIRECTORIES_1.get(0),
                                DIRECTORIES_1.get(0)
                        )))
                        .header("Authorization", accessToken)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void downloadDirectory_fail_invalidPathDirectory() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/download")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataDownloadDirectoryApi(
                                List.of("invalid", "path", "directory"),
                                List.of("invalid", "path", "directory"),
                                DIRECTORIES_1.get(0),
                                DIRECTORIES_1.get(0)
                        )))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void downloadDirectorySelect_fail_forbiddenDirectories() throws Exception {
        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                "ROLE_USER",
                "testDirectory",
                "testDirectory",
                "testDirectory",
                new ArrayList<>());
        directoryRedisRepository.save(directoryRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/v1/directory/download/select")
                        .content(objectMapper.writeValueAsString(new DataDownloadDirectorySelectApi(
                                List.of(new DataDownloadDirectorySelectPartApi(
                                        List.of("testDirectory"),
                                        List.of("testDirectory"),
                                        "testDirectory",
                                        "testDirectory"
                                ))
                        )))
                        .header("Authorization", accessToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden());

        directoryRedisRepository.delete(directoryRedis);
    }

    @Test
    public void downloadDirectorySelect_fail_forbiddenFiles() throws Exception {
        FileRedis fileRedis = new FileRedis(
                "login",
                "ROLE_USER",
                "testFile",
                "testFile",
                "testFile",
                new ArrayList<>());
        fileRedisRepository.save(fileRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/v1/directory/download/select")
                        .content(objectMapper.writeValueAsString(new DataDownloadDirectorySelectApi(
                                List.of(new DataDownloadDirectorySelectPartApi(
                                        new ArrayList<>(),
                                        new ArrayList<>(),
                                        "testFile",
                                        "testFile"
                                ))
                        )))
                        .header("Authorization", accessToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden());

        fileRedisRepository.delete(fileRedis);
    }

    @Test
    public void downloadDirectory_fail_isFrozenSource() throws Exception {
        String systemName1 = UUID.randomUUID().toString();
        String systemName2 = UUID.randomUUID().toString();

        prepareForIsFrozen(systemName1, systemName2);

        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/v1/directory/download")
                        .content(objectMapper.writeValueAsString(new DataDownloadDirectoryApi(
                                List.of(systemName1),
                                List.of(systemName1),
                                systemName2,
                                systemName2
                        )))
                        .header("Authorization", accessToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void downloadSelectDirectory_fail_isFrozenSource() throws Exception {
        String systemName1 = UUID.randomUUID().toString();
        String systemName2 = UUID.randomUUID().toString();

        prepareForIsFrozen(systemName1, systemName2);

        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/v1/directory/download/select")
                        .content(objectMapper.writeValueAsString(new DataDownloadDirectorySelectApi(
                            List.of(new DataDownloadDirectorySelectPartApi(
                                    List.of(systemName1),
                                    List.of(systemName1),
                                    systemName2,
                                    systemName2
                            ))
                        )))
                        .header("Authorization", accessToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    private void prepareForIsFrozen(String systemName1, String systemName2) throws NoSuchFileException {
        UtilsActionForFiles.createDirectories(
                List.of(systemName1, systemName2), buildDirectoryPath, logger);

        DirectoryRedis directoryRedis1 = new DirectoryRedis(
                "login",
                "ROLE_USER",
                systemName1,
                systemName1,
                Path.of(rootPath.getRootPath(), systemName1).toString(),
                List.of(UtilsAuthAction.LOGIN));
        DirectoryRedis directoryRedis2 = new DirectoryRedis(
                "login",
                "ROLE_USER",
                systemName2,
                systemName2,
                Path.of(rootPath.getRootPath(), systemName1, systemName2).toString(),
                List.of(UtilsAuthAction.LOGIN));
        RightUserFileSystemObjectRedis rightDirectory1 = new RightUserFileSystemObjectRedis(
                systemName1 + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.SHOW, OperationRights.DOWNLOAD));
        RightUserFileSystemObjectRedis rightDirectory2 = new RightUserFileSystemObjectRedis(
                systemName1 + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.SHOW, OperationRights.DOWNLOAD));
        FrozenFileSystemObjectRedis frozenFileSystemObjectRedis = new FrozenFileSystemObjectRedis(
                systemName1);

        directoryRedisRepository.saveAll(List.of(directoryRedis1, directoryRedis2));
        rightUserFileSystemObjectRedisRepository.saveAll(List.of(rightDirectory1, rightDirectory2));
        frozenFileSystemObjectRedisRepository.save(frozenFileSystemObjectRedis);
    }

    @Test
    public void downloadDirectory_success_forbidden() throws Exception {
        final String testFolder = "testFolder2";
        final String testFile = "testFile1";
        final Path testDirectoryPath = Path.of(rootPath.getRootPath(), testFolder);
        prepareDownload(testFolder, testFile);

        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/v1/directory/download")
                        .content(objectMapper.writeValueAsString(new DataDownloadDirectoryApi(
                                new ArrayList<>(),
                                new ArrayList<>(),
                                testFolder,
                                testFolder
                        )))
                        .header("Authorization", accessToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        fileRedisRepository.deleteById(testFile);
        directoryRedisRepository.deleteById(testFolder);
        rightUserFileSystemObjectRedisRepository.deleteAllById(List.of(
                testFile + "__" + UtilsAuthAction.LOGIN, testFolder + "__" + UtilsAuthAction.LOGIN));

        FileSystemUtils.deleteRecursively(testDirectoryPath);
    }

    @Test
    public void downloadDirectory_fail_isFrozenDirectory() throws Exception {
        final String testFolder = "testFolder2";
        final String testFile = "testFile1";
        final Path testDirectoryPath = Path.of(rootPath.getRootPath(), testFolder);
        prepareDownload(testFolder, testFile);

        FrozenFileSystemObjectRedis frozenFileSystemObjectRedis = new FrozenFileSystemObjectRedis(
                testFolder);

        frozenFileSystemObjectRedisRepository.save(frozenFileSystemObjectRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/v1/directory/download")
                        .content(objectMapper.writeValueAsString(new DataDownloadDirectoryApi(
                                new ArrayList<>(),
                                new ArrayList<>(),
                                testFolder,
                                testFolder
                        )))
                        .header("Authorization", accessToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        frozenFileSystemObjectRedisRepository.delete(frozenFileSystemObjectRedis);
        fileRedisRepository.deleteById(testFile);
        directoryRedisRepository.deleteById(testFolder);
        rightUserFileSystemObjectRedisRepository.deleteAllById(List.of(
                testFile + "__" + UtilsAuthAction.LOGIN, testFolder + "__" + UtilsAuthAction.LOGIN));

        FileSystemUtils.deleteRecursively(testDirectoryPath);
    }

    @Test
    public void downloadDirectorySelect_success_forbidden() throws Exception {
        final String testFolder = "testFolder2";
        final String testFile = "testFile1";
        final Path testDirectoryPath = Path.of(rootPath.getRootPath(), testFolder);
        prepareDownload(testFolder, testFile);

        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/v1/directory/download/select")
                        .content(objectMapper.writeValueAsString(new DataDownloadDirectorySelectApi(
                                List.of(new DataDownloadDirectorySelectPartApi(
                                        new ArrayList<>(),
                                        new ArrayList<>(),
                                        testFolder,
                                        testFolder
                                )))))
                        .header("Authorization", accessToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        fileRedisRepository.deleteById(testFile);
        directoryRedisRepository.deleteById(testFolder);
        rightUserFileSystemObjectRedisRepository.deleteAllById(List.of(
                testFile + "__" + UtilsAuthAction.LOGIN, testFolder + "__" + UtilsAuthAction.LOGIN));

        FileSystemUtils.deleteRecursively(testDirectoryPath);
    }

    @Test
    public void downloadDirectorySelect_fail_isFrozenDirectory() throws Exception {
        final String testFolder = "testFolder2";
        final String testFile = "testFile1";
        final Path testDirectoryPath = Path.of(rootPath.getRootPath(), testFolder);
        prepareDownload(testFolder, testFile);

        FrozenFileSystemObjectRedis frozenFileSystemObjectRedis = new FrozenFileSystemObjectRedis(
                testFolder);

        frozenFileSystemObjectRedisRepository.save(frozenFileSystemObjectRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/v1/directory/download/select")
                        .content(objectMapper.writeValueAsString(new DataDownloadDirectorySelectApi(
                                List.of(new DataDownloadDirectorySelectPartApi(
                                        new ArrayList<>(),
                                        new ArrayList<>(),
                                        testFolder,
                                        testFolder
                                )))))
                        .header("Authorization", accessToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        frozenFileSystemObjectRedisRepository.delete(frozenFileSystemObjectRedis);
        fileRedisRepository.deleteById(testFile);
        directoryRedisRepository.deleteById(testFolder);
        rightUserFileSystemObjectRedisRepository.deleteAllById(List.of(
                testFile + "__" + UtilsAuthAction.LOGIN, testFolder + "__" + UtilsAuthAction.LOGIN));

        FileSystemUtils.deleteRecursively(testDirectoryPath);
    }

    @SuppressWarnings("SameParameterValue")
    private void prepareDownload(String testFolder, String testFile) throws IOException {
        final Path testDirectoryPath = Path.of(rootPath.getRootPath(), testFolder);
        final Path testFilePath = Path.of(testDirectoryPath.toString(), testFile);
        Files.createDirectory(testDirectoryPath);
        Files.createFile(testFilePath);

        FileRedis fileRedis = new FileRedis(
                "login",
                "ROLE_USER",
                testFile,
                testFile,
                testFilePath.toString(),
                List.of(UtilsAuthAction.LOGIN));
        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                "ROLE_USER",
                testFolder,
                testFolder,
                testDirectoryPath.toString(),
                List.of(UtilsAuthAction.LOGIN));
        RightUserFileSystemObjectRedis rightDirectory = new RightUserFileSystemObjectRedis(
                testFolder + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.SHOW, OperationRights.DOWNLOAD));
        RightUserFileSystemObjectRedis rightFile = new RightUserFileSystemObjectRedis(
                testFile + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.SHOW, OperationRights.DOWNLOAD));
        fileRedisRepository.save(fileRedis);
        directoryRedisRepository.save(directoryRedis);
        rightUserFileSystemObjectRedisRepository.saveAll(List.of(rightDirectory, rightFile));
    }

}
