package com.zer0s2m.creeptenuous.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zer0s2m.creeptenuous.api.helpers.TestTagControllerApi;
import com.zer0s2m.creeptenuous.api.helpers.UtilsAuthAction;
import com.zer0s2m.creeptenuous.common.enums.Directory;
import com.zer0s2m.creeptenuous.common.http.ResponseUploadDirectoryApi;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.FileSystemUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagControllerApi
public class ControllerApiUploadDirectoryTests {
    Logger logger = LogManager.getLogger(ControllerApiUploadDirectoryTests.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ServiceBuildDirectoryPath buildDirectoryPath;

    private final String accessToken = UtilsAuthAction.builderHeader(UtilsAuthAction.generateAccessToken());

    @Test
    public void uploadDirectory_success() throws Exception {
        String testFileZip = "test-zip.zip";
        File testFile = new File("src/main/resources/test/" + testFileZip);
        InputStream targetStream = new FileInputStream(testFile);

        MvcResult result = mockMvc.perform(
                multipart("/api/v1/directory/upload")
                        .file(new MockMultipartFile(
                                "directory",
                                testFileZip,
                                "application/zip",
                                targetStream
                        ))
                        .queryParam("parents", "")
                        .queryParam("systemParents", "")
                        .header("Authorization",  accessToken)
                )
                .andExpect(status().isCreated())
                .andReturn();

        targetStream.close();

        String json = result.getResponse().getContentAsString();
        ResponseUploadDirectoryApi response = new ObjectMapper().readValue(json, ResponseUploadDirectoryApi.class);

        String testPath = buildDirectoryPath.build(new ArrayList<>());
        logger.info(String.format("Upload file zip: %s", testPath + Directory.SEPARATOR.get() + testFileZip));

        response.data().forEach(obj -> Assertions.assertTrue(Files.exists(obj.systemPath())));

        response.data().forEach(obj -> {
            if (Objects.equals(obj.realName(), "folder_1") || Objects.equals(obj.realName(), "folder_4")) {
                try {
                    FileSystemUtils.deleteRecursively(obj.systemPath());
                    logger.info(String.format("Delete folders for tests: %s", obj.systemPath()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void uploadDirectory_fail_invalidPathDirectory() throws Exception {
        mockMvc.perform(
                multipart("/api/v1/directory/upload")
                        .queryParam("parents", "invalid", "path", "directory")
                        .queryParam("systemParents", "invalid", "path", "directory")
                        .header("Authorization",  accessToken)
                )
                .andExpect(status().isBadRequest());
    }
}
