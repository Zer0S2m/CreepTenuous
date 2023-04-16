package com.zer0s2m.CreepTenuous.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zer0s2m.CreepTenuous.helpers.UtilsActionForFiles;
import com.zer0s2m.CreepTenuous.api.controllers.files.copy.data.DataCopyFile;
import com.zer0s2m.CreepTenuous.components.RootPath;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;
import com.zer0s2m.CreepTenuous.services.directory.manager.enums.Directory;

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

    private final String testFile1 = "testFile1.txt";
    private final String testFile2 = "testFile2.txt";
    private final String testFile3 = "testFile3.txt";

    DataCopyFile RECORD_1 = new DataCopyFile(
            testFile1,
            new ArrayList<>(),
            new ArrayList<>(),
            List.of("testFolder1")
    );
    DataCopyFile RECORD_2 = new DataCopyFile(
            null,
            Arrays.asList(testFile2, testFile3),
            new ArrayList<>(),
            List.of("testFolder2")
    );

    @Test
    public void copyOneFile_success() throws Exception {
        Path pathTestFile = UtilsActionForFiles.preparePreliminaryFiles(
                RECORD_1.nameFile(), RECORD_1.parents(), logger, buildDirectoryPath
        );
        Path pathTestFolder = Paths.get(
                rootPath.getRootPath() + Directory.SEPARATOR.get() + RECORD_1.toParents().get(0)
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
                )
                .andExpect(status().isCreated());

        Path newPathTestFile = UtilsActionForFiles.preparePreliminaryFiles(
                RECORD_1.nameFile(), RECORD_1.toParents(), logger, buildDirectoryPath
        );

        Assertions.assertTrue(Files.exists(pathTestFile));
        Assertions.assertTrue(Files.exists(newPathTestFile));

        UtilsActionForFiles.deleteFileAndWriteLog(pathTestFile, logger);
        UtilsActionForFiles.deleteFileAndWriteLog(newPathTestFile, logger);
        UtilsActionForFiles.deleteFileAndWriteLog(pathTestFolder, logger);
    }

    @Test
    public void copyMoteOneFile_success() throws Exception {
        List<String> nameFiles = RECORD_2.nameFiles();
        assert nameFiles != null;

        Path pathTestFile1 = UtilsActionForFiles.preparePreliminaryFiles(
                nameFiles.get(0), RECORD_2.parents(), logger, buildDirectoryPath
        );
        Path pathTestFile2 = UtilsActionForFiles.preparePreliminaryFiles(
                nameFiles.get(1), RECORD_2.parents(), logger, buildDirectoryPath
        );
        Path pathTestFolder = Paths.get(
                rootPath.getRootPath() + Directory.SEPARATOR.get() + RECORD_2.toParents().get(0)
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
                )
                .andExpect(status().isCreated());

        Path newPathTestFile1 = UtilsActionForFiles.preparePreliminaryFiles(
                nameFiles.get(0), RECORD_2.toParents(), logger, buildDirectoryPath
        );
        Path newPathTestFile2 = UtilsActionForFiles.preparePreliminaryFiles(
                nameFiles.get(1), RECORD_2.toParents(), logger, buildDirectoryPath
        );

        Assertions.assertTrue(Files.exists(pathTestFile1));
        Assertions.assertTrue(Files.exists(pathTestFile2));
        Assertions.assertTrue(Files.exists(newPathTestFile1));
        Assertions.assertTrue(Files.exists(newPathTestFile2));

        UtilsActionForFiles.deleteFileAndWriteLog(pathTestFile1, logger);
        UtilsActionForFiles.deleteFileAndWriteLog(pathTestFile2, logger);
        UtilsActionForFiles.deleteFileAndWriteLog(newPathTestFile1, logger);
        UtilsActionForFiles.deleteFileAndWriteLog(newPathTestFile2, logger);
        UtilsActionForFiles.deleteFileAndWriteLog(pathTestFolder, logger);
    }

    @Test
    public void copyFile_fail_notValidParents() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/file/copy")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new DataCopyFile(
                                        "file.txt",
                                        null,
                                        null,
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
                        .content(objectMapper.writeValueAsString(
                                new DataCopyFile(
                                        "file.txt",
                                        null,
                                        new ArrayList<>(),
                                        null
                                )
                        ))
                )
                .andExpect(status().isBadRequest());
    }
}
