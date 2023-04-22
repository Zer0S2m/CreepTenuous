package com.zer0s2m.CreepTenuous.api.controllers;

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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.FileSystemUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ControllerApiUploadDirectoryTests {
    Logger logger = LogManager.getLogger(ControllerApiUploadDirectoryTests.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ServiceBuildDirectoryPath buildDirectoryPath;

    @Test
    public void uploadDirectory_success() throws Exception {
        String testFileZip = "test-zip.zip";
        File testFile = new File("src/main/resources/test/" + testFileZip);
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
                )
                .andExpect(status().isCreated());

        targetStream.close();

        String testPath = buildDirectoryPath.build(new ArrayList<>());
        logger.info(String.format("Upload file zip: %s", testPath + Directory.SEPARATOR.get() + testFileZip));

        Assertions.assertTrue(Files.exists(Path.of(testPath, "folder_1", "test_image_1.jpeg")));
        Assertions.assertTrue(Files.exists(Path.of(testPath, "folder_1", "folder_2", "test_image_1.jpeg")));
        Assertions.assertTrue(Files.exists(Path.of(testPath, "folder_1", "folder_2", "test_image_2.jpeg")));
        Assertions.assertTrue(Files.exists(Path.of(testPath, "folder_1", "folder_2", "test-file-1.txt")));
        Assertions.assertTrue(Files.exists(Path.of(testPath, "folder_1", "folder_3", "test-file-1.txt")));
        Assertions.assertTrue(Files.exists(Path.of(testPath, "folder_4", "test_image_1.jpeg")));

        Path pathTestFolder1 = Path.of(buildDirectoryPath.build(List.of("folder_1")));
        Path pathTestFolder2 = Path.of(buildDirectoryPath.build(List.of("folder_4")));
        FileSystemUtils.deleteRecursively(pathTestFolder1);
        FileSystemUtils.deleteRecursively(pathTestFolder2);

        logger.info(String.format("Delete folders for tests: %s; %s", pathTestFolder1, pathTestFolder2));
    }

    @Test
    public void uploadDirectory_fail_invalidPathDirectory() throws Exception {
        mockMvc.perform(
                multipart("/api/v1/directory/upload")
                        .queryParam("parents", "invalid", "path", "directory")
                )
                .andExpect(status().isBadRequest());
    }
}
