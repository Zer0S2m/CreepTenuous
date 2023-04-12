package com.zer0s2m.CreepTenuous.services.files;

import com.zer0s2m.CreepTenuous.Helpers.UtilsActionForFiles;
import com.zer0s2m.CreepTenuous.api.controllers.files.move.data.DataMoveFile;
import com.zer0s2m.CreepTenuous.components.RootPath;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.BuildDirectoryPath;
import com.zer0s2m.CreepTenuous.services.common.collectRootPath.impl.CollectRootPath;
import com.zer0s2m.CreepTenuous.services.directory.manager.enums.Directory;
import com.zer0s2m.CreepTenuous.services.files.move.services.impl.ServiceMoveFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SpringBootTest(classes = {
        ServiceMoveFile.class,
        BuildDirectoryPath.class,
        CollectRootPath.class,
        RootPath.class,
})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ServiceMoveFileTests {
    Logger logger = LogManager.getLogger(ServiceMoveFileTests.class);

    @Autowired
    private ServiceMoveFile service;

    @Autowired
    private BuildDirectoryPath buildDirectoryPath;

    @Autowired
    private RootPath rootPath;

    DataMoveFile RECORD_1 = new DataMoveFile(
            "testFile1.txt",
            null,
            new ArrayList<>(),
            List.of("testFolder1")
    );
    DataMoveFile RECORD_2 = new DataMoveFile(
            null,
            Arrays.asList("testFile2.txt", "testFile3.txt"),
            new ArrayList<>(),
            List.of("testFolder2")
    );

    DataMoveFile INVALID_RECORD_NOT_IS_EXISTS_FILE = new DataMoveFile(
            "notIsExistsFile.txt",
            null,
            new ArrayList<>(),
            List.of("testFolder1")
    );

    DataMoveFile INVALID_RECORD_INVALID_PATH_DIRECTORY = new DataMoveFile(
            "testFile1.txt",
            null,
            new ArrayList<>(),
            Arrays.asList("invalid", "path", "directory")
    );

    @Test
    public void moveOneFile_success() throws IOException {
        Path pathTestFile = Paths.get(
                buildDirectoryPath.build(RECORD_1.parents()) + Directory.SEPARATOR.get() + RECORD_1.nameFile()
        );
        Path pathTestFolder = Paths.get(
                rootPath.getRootPath() + Directory.SEPARATOR.get() + RECORD_1.toParents().get(0)
        );

        Files.createFile(pathTestFile);
        Files.createDirectory(pathTestFolder);

        Assertions.assertTrue(Files.exists(pathTestFile));
        Assertions.assertTrue(Files.exists(pathTestFolder));

        logger.info("Create file for tests: " + pathTestFile);
        logger.info("Create folder for tests: " + pathTestFolder);

        Path newPathTestFile = Paths.get(
                buildDirectoryPath.build(RECORD_1.toParents()) + Directory.SEPARATOR.get() + RECORD_1.nameFile()
        );

        service.move(pathTestFile, newPathTestFile);

        Assertions.assertFalse(Files.exists(pathTestFile));
        Assertions.assertTrue(Files.exists(newPathTestFile));

        UtilsActionForFiles.deleteFileAndWriteLog(newPathTestFile, logger);
        UtilsActionForFiles.deleteFileAndWriteLog(pathTestFolder, logger);
    }

    @Test
    public void moveMoreOneFile_success() throws IOException {
        Path pathTestFile1 = Paths.get(
                buildDirectoryPath.build(RECORD_2.parents())
                    + Directory.SEPARATOR.get()
                    + Objects.requireNonNull(RECORD_2.nameFiles()).get(0)
        );
        Path pathTestFile2 = Paths.get(
                buildDirectoryPath.build(RECORD_2.parents())
                        + Directory.SEPARATOR.get()
                        + Objects.requireNonNull(RECORD_2.nameFiles()).get(1)
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

        Path newPathTestFile1 = Paths.get(
                buildDirectoryPath.build(RECORD_2.toParents())
                    + Directory.SEPARATOR.get()
                    + Objects.requireNonNull(RECORD_2.nameFiles()).get(0)
        );
        Path newPathTestFile2 = Paths.get(
                buildDirectoryPath.build(RECORD_2.toParents())
                        + Directory.SEPARATOR.get()
                        + Objects.requireNonNull(RECORD_2.nameFiles()).get(1)
        );

        service.move(
                Objects.requireNonNull(RECORD_2.nameFiles()),
                RECORD_2.parents(),
                RECORD_2.toParents()
        );

        Assertions.assertFalse(Files.exists(pathTestFile1));
        Assertions.assertFalse(Files.exists(pathTestFile2));
        Assertions.assertTrue(Files.exists(newPathTestFile1));
        Assertions.assertTrue(Files.exists(newPathTestFile2));

        UtilsActionForFiles.deleteFileAndWriteLog(newPathTestFile1, logger);
        UtilsActionForFiles.deleteFileAndWriteLog(newPathTestFile2, logger);
        UtilsActionForFiles.deleteFileAndWriteLog(pathTestFolder, logger);
    }

    @Test
    public void moveFile_fail_notIsExistsFile() {
        Assertions.assertThrows(
                NoSuchFileException.class,
                () -> service.move(
                        INVALID_RECORD_NOT_IS_EXISTS_FILE.nameFile(),
                        INVALID_RECORD_NOT_IS_EXISTS_FILE.parents(),
                        INVALID_RECORD_NOT_IS_EXISTS_FILE.toParents()
                )
        );
    }

    @Test
    public void moveFile_fail_invalidPathDirectory() {
        Assertions.assertThrows(
                NoSuchFileException.class,
                () -> service.move(
                        INVALID_RECORD_INVALID_PATH_DIRECTORY.nameFile(),
                        INVALID_RECORD_INVALID_PATH_DIRECTORY.parents(),
                        INVALID_RECORD_INVALID_PATH_DIRECTORY.toParents()
                )
        );
    }
}
