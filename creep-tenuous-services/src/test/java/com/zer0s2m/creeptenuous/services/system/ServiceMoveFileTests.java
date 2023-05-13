package com.zer0s2m.creeptenuous.services.system;

import com.zer0s2m.creeptenuous.common.components.RootPath;
import com.zer0s2m.creeptenuous.common.data.DataMoveFileApi;
import com.zer0s2m.creeptenuous.services.helpers.TestTagServiceFileSystem;
import com.zer0s2m.creeptenuous.services.helpers.UtilsActionForFiles;
import com.zer0s2m.creeptenuous.services.system.core.CollectRootPathImpl;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceMoveFileImpl;
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
        ServiceMoveFileImpl.class,
        ServiceBuildDirectoryPath.class,
        CollectRootPathImpl.class,
        RootPath.class,
})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceFileSystem
public class ServiceMoveFileTests {
    Logger logger = LogManager.getLogger(ServiceMoveFileTests.class);

    @Autowired
    private ServiceMoveFile service;

    @Autowired
    private ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    private RootPath rootPath;

    DataMoveFileApi RECORD_1 = new DataMoveFileApi(
            "testFile1.txt",
            "testFile1.txt",
            null,
            null,
            new ArrayList<>(),
            new ArrayList<>(),
            List.of("testFolder1"),
            List.of("testFolder1")
    );
    DataMoveFileApi RECORD_2 = new DataMoveFileApi(
            null,
            null,
            Arrays.asList("testFile2.txt", "testFile3.txt"),
            Arrays.asList("testFile2.txt", "testFile3.txt"),
            new ArrayList<>(),
            new ArrayList<>(),
            List.of("testFolder2"),
            List.of("testFolder2")
    );

    DataMoveFileApi INVALID_RECORD_NOT_IS_EXISTS_FILE = new DataMoveFileApi(
            "notIsExistsFile.txt",
            "notIsExistsFile.txt",
            null,
            null,
            new ArrayList<>(),
            new ArrayList<>(),
            List.of("testFolder1"),
            List.of("testFolder1")
    );

    DataMoveFileApi INVALID_RECORD_INVALID_PATH_DIRECTORY = new DataMoveFileApi(
            "testFile1.txt",
            "testFile1.txt",
            null,
            null,
            new ArrayList<>(),
            new ArrayList<>(),
            Arrays.asList("invalid", "path", "directory"),
            Arrays.asList("invalid", "path", "directory")
    );

    @Test
    public void moveOneFile_success() throws IOException {
        Path pathTestFile = UtilsActionForFiles.preparePreliminaryFiles(
                RECORD_1.nameFile(), RECORD_1.parents(), logger, buildDirectoryPath
        );
        Path pathTestFolder = Paths.get(
                rootPath.getRootPath(), RECORD_1.toParents().get(0)
        );

        Files.createFile(pathTestFile);
        Files.createDirectory(pathTestFolder);

        Assertions.assertTrue(Files.exists(pathTestFile));
        Assertions.assertTrue(Files.exists(pathTestFolder));

        logger.info("Create folder for tests: " + pathTestFolder);

        Path newPathTestFile = UtilsActionForFiles.preparePreliminaryFiles(
                RECORD_1.nameFile(), RECORD_1.toParents(), logger, buildDirectoryPath
        );

        service.move(pathTestFile, newPathTestFile);

        Assertions.assertFalse(Files.exists(pathTestFile));
        Assertions.assertTrue(Files.exists(newPathTestFile));

        UtilsActionForFiles.deleteFileAndWriteLog(newPathTestFile, logger);
        UtilsActionForFiles.deleteFileAndWriteLog(pathTestFolder, logger);
    }

    @Test
    public void moveMoreOneFile_success() throws IOException {
        List<String> nameFiles = Objects.requireNonNull(RECORD_2.nameFiles());
        Path pathTestFile1 = UtilsActionForFiles.preparePreliminaryFiles(
                nameFiles.get(0), RECORD_2.parents(), logger, buildDirectoryPath
        );
        Path pathTestFile2 =UtilsActionForFiles.preparePreliminaryFiles(
                nameFiles.get(1), RECORD_2.parents(), logger, buildDirectoryPath
        );
        Path pathTestFolder = Paths.get(
                rootPath.getRootPath(), RECORD_2.toParents().get(0)
        );

        Files.createFile(pathTestFile1);
        Files.createFile(pathTestFile2);
        Files.createDirectory(pathTestFolder);

        logger.info("Create folder for tests: " + pathTestFolder);

        Path newPathTestFile1 = UtilsActionForFiles.preparePreliminaryFiles(
                nameFiles.get(0), RECORD_2.toParents(), logger, buildDirectoryPath
        );
        Path newPathTestFile2 = UtilsActionForFiles.preparePreliminaryFiles(
                nameFiles.get(1), RECORD_2.toParents(), logger, buildDirectoryPath
        );

        service.move(nameFiles, RECORD_2.parents(), RECORD_2.toParents());

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
