package com.zer0s2m.CreepTenuous.api.controllers;

import com.zer0s2m.CreepTenuous.api.controllers.files.move.data.DataMoveFile;
import com.zer0s2m.CreepTenuous.components.RootPath;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.BuildDirectoryPath;
import com.zer0s2m.CreepTenuous.services.directory.manager.enums.Directory;

import com.fasterxml.jackson.databind.ObjectMapper;
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
public class ControllerApiMoveFileTests {
    Logger logger = LogManager.getLogger(ControllerApiMoveFileTests.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BuildDirectoryPath buildDirectoryPath;

    @Autowired
    private RootPath rootPath;

    private final String testFile1 = "testFile1.txt";
    private final String testFile2 = "testFile2.txt";
    private final String testFile3 = "testFile3.txt";

    DataMoveFile RECORD_1 = new DataMoveFile(
            testFile1,
            new ArrayList<>(),
            new ArrayList<>(),
            List.of("testFolder1")
    );
    DataMoveFile RECORD_2 = new DataMoveFile(
            null,
            Arrays.asList(testFile2, testFile3),
            new ArrayList<>(),
            List.of("testFolder2")
    );

    @Test
    public void moveOneFile_success() throws Exception {
        Path pathTestFile = Paths.get(
                buildDirectoryPath.build(RECORD_1.parents()) + Directory.SEPARATOR.get() + RECORD_1.nameFile()
        );
        Path pathTestFolder = Paths.get(
                rootPath.getRootPath() + Directory.SEPARATOR.get() + RECORD_1.toParents().get(0)
        );
        Files.createFile(pathTestFile);
        Files.createDirectory(pathTestFolder);
        logger.info("Create file for tests: " + pathTestFile);
        logger.info("Create folder for tests: " + pathTestFolder);

        Assertions.assertTrue(Files.exists(pathTestFile));
        Assertions.assertTrue(Files.exists(pathTestFolder));

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/file/move")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(RECORD_1))
                )
                .andExpect(status().isCreated());

        Path newPathTestFile = Paths.get(
                buildDirectoryPath.build(RECORD_1.toParents()) + Directory.SEPARATOR.get() + RECORD_1.nameFile()
        );

        Assertions.assertFalse(Files.exists(pathTestFile));
        Assertions.assertTrue(Files.exists(newPathTestFile));

        logger.info(
                "Is deleted file for tests: " + Files.deleteIfExists(newPathTestFile) + " (" + newPathTestFile + ")"
        );
        logger.info(
                "Is deleted folder for tests: " + Files.deleteIfExists(pathTestFolder) + " (" + pathTestFolder + ")"
        );
    }

    @Test
    public void moveMoreOneFile_success() throws Exception {
        List<String> nameFiles = RECORD_2.nameFiles();

        assert nameFiles != null;
        Path pathTestFile1 = Paths.get(
                buildDirectoryPath.build(RECORD_2.parents()) + Directory.SEPARATOR.get() + nameFiles.get(0)
        );
        Path pathTestFile2 = Paths.get(
                buildDirectoryPath.build(RECORD_2.parents()) + Directory.SEPARATOR.get() + nameFiles.get(1)
        );
        Path pathTestFolder = Paths.get(
                rootPath.getRootPath() + Directory.SEPARATOR.get() + RECORD_2.toParents().get(0)
        );
        Files.createFile(pathTestFile1);
        Files.createFile(pathTestFile2);
        Files.createDirectory(pathTestFolder);

        logger.info("Create file for tests: " + pathTestFile1);
        logger.info("Create file for tests: " + pathTestFile2);
        logger.info("Create folder for tests: " + pathTestFolder);

        Assertions.assertTrue(Files.exists(pathTestFile1));
        Assertions.assertTrue(Files.exists(pathTestFile2));
        Assertions.assertTrue(Files.exists(pathTestFolder));

        this.mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/file/move")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(RECORD_2))
                )
                .andExpect(status().isCreated());

        Path newPathTestFile1 = Paths.get(
                buildDirectoryPath.build(RECORD_2.toParents()) + Directory.SEPARATOR.get() + nameFiles.get(0)
        );
        Path newPathTestFile2 = Paths.get(
                buildDirectoryPath.build(RECORD_2.toParents()) + Directory.SEPARATOR.get() + nameFiles.get(1)
        );

        Assertions.assertFalse(Files.exists(pathTestFile1));
        Assertions.assertFalse(Files.exists(pathTestFile2));
        Assertions.assertTrue(Files.exists(newPathTestFile1));
        Assertions.assertTrue(Files.exists(newPathTestFile2));

        logger.info(
                "Is deleted file for tests: " + Files.deleteIfExists(newPathTestFile1) + " (" + newPathTestFile1 + ")"
        );
        logger.info(
                "Is deleted file for tests: " + Files.deleteIfExists(newPathTestFile2) + " (" + newPathTestFile2 + ")"
        );
        logger.info(
                "Is deleted folder for tests: " + Files.deleteIfExists(pathTestFolder) + " (" + pathTestFolder + ")"
        );
    }

    @Test
    @DisplayName("Failed moving file")
    public void moveFile_fail_notValidParents() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/file/move")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new DataMoveFile(
                                        "testFile.txt",
                                        null,
                                        new ArrayList<>(),
                                        Arrays.asList("not", "valid", "directory")
                                )
                        ))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Failed moving multiple files")
    public void moveFiles_fail_fileNotIsExists() throws Exception {
        MockHttpServletRequestBuilder builderRequest = MockMvcRequestBuilders.post("/api/v1/file/move")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(builderRequest
                .content(objectMapper.writeValueAsString(
                        new DataMoveFile(
                                "fileNotIsExists.txt",
                                null,
                                new ArrayList<>(),
                                new ArrayList<>()
                        )
                )))
                .andExpect(status().isNotFound());
        this.mockMvc.perform(builderRequest
                        .content(objectMapper.writeValueAsString(
                                new DataMoveFile(
                                        null,
                                        Arrays.asList("fileNotIsExists1.txt", "fileNotIsExists2.txt"),
                                        new ArrayList<>(),
                                        new ArrayList<>()
                                )
                        ))
                )
                .andExpect(status().isNotFound());
    }
}
