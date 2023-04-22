package com.zer0s2m.CreepTenuous.api.controllers;

import com.zer0s2m.CreepTenuous.helpers.UtilsActionForFiles;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;

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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ControllerApiUploadFileTests {
    Logger logger = LogManager.getLogger(ControllerApiUploadFileTests.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ServiceBuildDirectoryPath buildDirectoryPath;

    private final String nameTestFile1 = "test_image_1.jpeg";
    private final String nameTestFile2 = "test_image_2.jpeg";

    @Test
    public void uploadOneFile_success() throws Exception {
        File testFile = new File("src/main/resources/test/" + nameTestFile1);
        InputStream targetStream = new FileInputStream(testFile);

        Path pathTestUploadFile = UtilsActionForFiles.preparePreliminaryFiles(
                nameTestFile1, new ArrayList<>(), logger, buildDirectoryPath
        );

        mockMvc.perform(
                multipart("/api/v1/file/upload")
                        .file(getMockFile(nameTestFile1, targetStream))
                        .queryParam("parents", "")
        )
                .andExpect(status().isOk());

        targetStream.close();

        Assertions.assertTrue(Files.exists(pathTestUploadFile));

        UtilsActionForFiles.deleteFileAndWriteLog(pathTestUploadFile, logger);

        Assertions.assertFalse(Files.exists(pathTestUploadFile));
    }

    @Test
    public void uploadMoreOneFile_success() throws Exception {
        File testFile1 = new File("src/main/resources/test/" + nameTestFile1);
        File testFile2 = new File("src/main/resources/test/" + nameTestFile2);
        InputStream targetStream1 = new FileInputStream(testFile1);
        InputStream targetStream2 = new FileInputStream(testFile2);

        mockMvc.perform(
                multipart("/api/v1/file/upload")
                        .file(getMockFile(nameTestFile1, targetStream1))
                        .file(getMockFile(nameTestFile2, targetStream2))
                        .queryParam("parents", "")
                )
                .andExpect(status().isOk());

        targetStream1.close();
        targetStream2.close();

        Path pathTestUploadFile1 = UtilsActionForFiles.preparePreliminaryFiles(
                nameTestFile1, new ArrayList<>(), logger, buildDirectoryPath
        );
        Path pathTestUploadFile2 = UtilsActionForFiles.preparePreliminaryFiles(
                nameTestFile2, new ArrayList<>(), logger, buildDirectoryPath
        );

        Assertions.assertTrue(Files.exists(pathTestUploadFile1));
        Assertions.assertTrue(Files.exists(pathTestUploadFile2));

        UtilsActionForFiles.deleteFileAndWriteLog(pathTestUploadFile1, logger);
        UtilsActionForFiles.deleteFileAndWriteLog(pathTestUploadFile2, logger);

        Assertions.assertFalse(Files.exists(pathTestUploadFile1));
        Assertions.assertFalse(Files.exists(pathTestUploadFile2));
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
        File testFile = new File("src/main/resources/test/" + nameTestFile2);
        InputStream targetStream = new FileInputStream(testFile);

        mockMvc.perform(
                multipart("/api/v1/file/upload")
                        .file(getMockFile(nameTestFile2, targetStream))
                        .queryParam("parents", "invalid", "path", "directory")
                )
                .andExpect(status().isNotFound());

        targetStream.close();

        Path pathTestUploadFile = UtilsActionForFiles.preparePreliminaryFiles(
                nameTestFile2, new ArrayList<>(), logger, buildDirectoryPath
        );
        Assertions.assertFalse(Files.exists(pathTestUploadFile));
    }
}
