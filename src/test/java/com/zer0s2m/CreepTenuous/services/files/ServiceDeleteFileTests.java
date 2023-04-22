package com.zer0s2m.CreepTenuous.services.files;

import com.zer0s2m.CreepTenuous.api.controllers.common.exceptions.NoSuchFileExistsException;
import com.zer0s2m.CreepTenuous.api.controllers.files.delete.data.DataDeleteFile;
import com.zer0s2m.CreepTenuous.components.RootPath;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;
import com.zer0s2m.CreepTenuous.services.common.collectRootPath.impl.CollectRootPath;
import com.zer0s2m.CreepTenuous.services.directory.manager.enums.Directory;
import com.zer0s2m.CreepTenuous.services.files.delete.services.impl.ServiceDeleteFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

@SpringBootTest(classes = {
        ServiceDeleteFile.class,
        ServiceBuildDirectoryPath.class,
        CollectRootPath.class,
        RootPath.class,
})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ServiceDeleteFileTests {
    Logger logger = LogManager.getLogger(ServiceDeleteFileTests.class);

    @Autowired
    private ServiceDeleteFile service;

    @Autowired
    private ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    private RootPath rootPath;

    private final String testFile1 = "testFile1.txt";

    DataDeleteFile RECORD_1 = new DataDeleteFile(testFile1, new ArrayList<>());
    DataDeleteFile INVALID_RECORD_NOT_IS_EXISTS = new DataDeleteFile("notFileIsExists.txt", new ArrayList<>());
    DataDeleteFile INVALID_RECORD_INVALID_PATH_DIRECTORY = new DataDeleteFile(
            "testFile.txt",
            Arrays.asList("invalid", "path", "directory")
    );

    @Test
    public void deleteFile_success() throws IOException, NoSuchFileExistsException {
        Path pathTestFile = Paths.get(
                buildDirectoryPath.build(RECORD_1.parents()), RECORD_1.nameFile()
        );
        Files.createFile(pathTestFile);
        logger.info("Create file for tests: " + pathTestFile);

        Assertions.assertTrue(Files.exists(pathTestFile));

        service.delete(RECORD_1.nameFile(), RECORD_1.parents());

        Assertions.assertFalse(Files.exists(pathTestFile));

        logger.info(
                "Is deleted file for tests: " + Files.exists(pathTestFile) + " (" + pathTestFile + ")"
        );
    }

    @Test
    public void deleteFile_fail_fileNotIsExists() throws IOException {
        Path pathTestFile = Paths.get(
                buildDirectoryPath.build(INVALID_RECORD_NOT_IS_EXISTS.parents()),
                INVALID_RECORD_NOT_IS_EXISTS.nameFile()
        );

        Assertions.assertFalse(Files.exists(pathTestFile));
        Assertions.assertThrows(
                NoSuchFileExistsException.class,
                () -> service.delete(INVALID_RECORD_NOT_IS_EXISTS.nameFile(), INVALID_RECORD_NOT_IS_EXISTS.parents())
        );
    }

    @Test
    public void deleteFile_fail_invalidPathDirectory() {
        StringBuilder rawDirectory = new StringBuilder();
        for (String part : INVALID_RECORD_INVALID_PATH_DIRECTORY.parents()) {
            rawDirectory.append(Directory.SEPARATOR.get()).append(part);
        }
        Path pathTestFile = Path.of(rootPath.getRootPath(), String.valueOf(rawDirectory));

        Assertions.assertFalse(Files.exists(pathTestFile));
        Assertions.assertThrows(
                NoSuchFileException.class,
                () -> service.delete(
                        INVALID_RECORD_INVALID_PATH_DIRECTORY.nameFile(),
                        INVALID_RECORD_INVALID_PATH_DIRECTORY.parents()
                )
        );
    }
}
