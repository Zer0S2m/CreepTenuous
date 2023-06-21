package com.zer0s2m.creeptenuous.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zer0s2m.creeptenuous.api.helpers.UtilsActionForFiles;
import com.zer0s2m.creeptenuous.common.data.DataCreateFileApi;
import com.zer0s2m.creeptenuous.common.enums.Directory;
import com.zer0s2m.creeptenuous.common.enums.ExceptionFile;
import com.zer0s2m.creeptenuous.common.enums.TypeFile;
import com.zer0s2m.creeptenuous.common.exceptions.messages.ExceptionNotDirectoryMsg;
import com.zer0s2m.creeptenuous.common.exceptions.messages.FileAlreadyExistsMsg;
import com.zer0s2m.creeptenuous.common.exceptions.messages.NotFoundTypeFileMsg;
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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.ArrayList;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagControllerApi
public class ControllerApiCreateFileTests {
    Logger logger = LogManager.getLogger(ControllerApiCreateFileTests.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ServiceBuildDirectoryPath buildDirectoryPath;

    private final String accessToken = UtilsAuthAction.builderHeader(UtilsAuthAction.generateAccessToken());

    protected String nameTestFile1 = "testFile_1";
    protected String nameTestFile2 = "testFile_2";
    protected String nameTestFile3 = "testFile_3";
    protected String nameTestFile4 = "testFile_4";

    DataCreateFileApi RECORD_1 = new DataCreateFileApi(1, nameTestFile1, new ArrayList<>(), new ArrayList<>());
    DataCreateFileApi RECORD_2 = new DataCreateFileApi(2, nameTestFile2, new ArrayList<>(), new ArrayList<>());
    DataCreateFileApi RECORD_3 = new DataCreateFileApi(3, nameTestFile3, new ArrayList<>(), new ArrayList<>());
    DataCreateFileApi INVALID_RECORD_TYPE_FILE = new DataCreateFileApi(
            9999,
            "failFile",
            new ArrayList<>(),
            new ArrayList<>()
    );
    DataCreateFileApi INVALID_RECORD_PATH_DIRECTORY = new DataCreateFileApi(
            1,
            "failFile",
            Arrays.asList("invalid", "path", "directory"),
            Arrays.asList("invalid", "path", "directory")
    );

    @Test
    public void createFile_success() throws Exception {
        for (DataCreateFileApi record : Arrays.asList(RECORD_1, RECORD_2, RECORD_3)) {
            this.mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/file/create")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
                            .content(objectMapper.writeValueAsString(record))
            )
                    .andExpect(status().isCreated());
        }
    }

    @Test
    public void createFile_fail_invalidTypeFile() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/file/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(INVALID_RECORD_TYPE_FILE))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(
                                new NotFoundTypeFileMsg(ExceptionFile.NOT_FOUND_TYPE_FILE.get())
                        )
                ));
    }

    @Deprecated
    public void createFile_fail_invalidPathDirectory() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/file/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(INVALID_RECORD_PATH_DIRECTORY))
                )
                .andExpect(status().isNotFound())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(
                                new ExceptionNotDirectoryMsg(Directory.NOT_FOUND_DIRECTORY.get())
                        )
                ));
    }

    /**
     * @deprecated
     */
    public void createFile_fail_fileIsExists() throws Exception {
        Path testFile = UtilsActionForFiles.preparePreliminaryFiles(
                nameTestFile4 + "." + TypeFile.TXT.getExtension(), new ArrayList<>(), logger, buildDirectoryPath
        );
        Files.createFile(testFile);

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/file/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(
                                new DataCreateFileApi(1, nameTestFile4, new ArrayList<>(), new ArrayList<>())
                        ))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(
                                new FileAlreadyExistsMsg(ExceptionFile.FILE_ALREADY_EXISTS.get())
                        )
                ));

        logger.info("Is deleted file for tests: " + Files.deleteIfExists(testFile) + " (" + testFile + ")");
    }

    @Test
    public void createFile_fail_notValidNameFile() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/file/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new DataCreateFileApi(1, "", new ArrayList<>(), new ArrayList<>())
                        ))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createFile_fail_notValidTypeFile() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/file/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new DataCreateFileApi(
                                        null,
                                        "testFile",
                                        new ArrayList<>(),
                                        new ArrayList<>()
                                )
                        ))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createFile_fail_notValidParents() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/file/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new DataCreateFileApi(1, "testFile", null, null)
                        ))
                )
                .andExpect(status().isBadRequest());
    }
}
