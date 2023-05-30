package com.zer0s2m.creeptenuous.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zer0s2m.creeptenuous.api.helpers.TestTagControllerApi;
import com.zer0s2m.creeptenuous.api.helpers.UtilsAuthAction;
import com.zer0s2m.creeptenuous.api.helpers.UtilsActionForFiles;
import com.zer0s2m.creeptenuous.common.components.RootPath;
import com.zer0s2m.creeptenuous.common.data.DataCopyFileApi;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
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
}
