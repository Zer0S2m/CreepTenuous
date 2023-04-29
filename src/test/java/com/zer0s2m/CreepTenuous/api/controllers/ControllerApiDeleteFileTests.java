package com.zer0s2m.CreepTenuous.api.controllers;

import com.zer0s2m.CreepTenuous.helpers.UtilsActionForFiles;
import com.zer0s2m.CreepTenuous.api.controllers.common.exceptions.messages.NoSuchFileExists;
import com.zer0s2m.CreepTenuous.api.controllers.files.delete.data.DataDeleteFile;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;
import com.zer0s2m.CreepTenuous.services.core.Directory;
import com.zer0s2m.CreepTenuous.services.directory.manager.exceptions.messages.ExceptionNotDirectoryMsg;
import com.zer0s2m.CreepTenuous.services.core.ExceptionFile;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.ArrayList;
import java.util.Arrays;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ControllerApiDeleteFileTests {
    Logger logger = LogManager.getLogger(ControllerApiDeleteFileTests.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ServiceBuildDirectoryPath buildDirectoryPath;

    private final String testFile1 = "testFile1.txt";
    private final String testFile2 = "testFile2.txt";

    DataDeleteFile RECORD_1 = new DataDeleteFile(testFile1, new ArrayList<>());
    DataDeleteFile RECORD_2 = new DataDeleteFile(testFile2, new ArrayList<>());
    DataDeleteFile INVALID_RECORD_PATH_DIRECTORY = new DataDeleteFile(
            "failTestFile",
            Arrays.asList("invalid", "path", "directory")
    );
    DataDeleteFile INVALID_RECORD_NOT_EXISTS_FILE = new DataDeleteFile("notExistsFileFail", new ArrayList<>());

    @Test
    public void deleteFile_success() throws Exception {
        for (DataDeleteFile record : Arrays.asList(RECORD_1, RECORD_2)) {
            Path pathTestFile = UtilsActionForFiles.preparePreliminaryFiles(
                    record.nameFile(), record.parents(), logger, buildDirectoryPath
            );
            Files.createFile(pathTestFile);

            this.mockMvc.perform(
                    MockMvcRequestBuilders.delete("/api/v1/file/delete")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(record))
                    )
                    .andExpect(status().isNoContent());
            Assertions.assertFalse(Files.exists(pathTestFile));

            UtilsActionForFiles.deleteFileAndWriteLog(pathTestFile, logger);
        }
    }

    @Test
    public void deleteFile_file_invalidPathDirectory() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/file/delete")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(INVALID_RECORD_PATH_DIRECTORY))
                )
                .andExpect(status().isNotFound())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(
                                new ExceptionNotDirectoryMsg(Directory.NOT_FOUND_DIRECTORY.get())
                        )
                ));
    }

    @Test
    public void deleteFile_file_notIsExists() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/file/delete")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(INVALID_RECORD_NOT_EXISTS_FILE))
                )
                .andExpect(status().isNotFound())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(
                                new NoSuchFileExists(ExceptionFile.FILE_NOT_EXISTS.get())
                        )
                ));
    }

    @Test
    public void deleteFile_fail_notValidNameFile() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/file/delete")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new DataDeleteFile("", new ArrayList<>())
                        ))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteFile_fail_notValidParents() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/file/delete")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new DataDeleteFile("testFile", null)
                        ))
                )
                .andExpect(status().isBadRequest());
    }
}
