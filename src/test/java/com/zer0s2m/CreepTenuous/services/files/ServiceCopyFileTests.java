package com.zer0s2m.CreepTenuous.services.files;

import com.zer0s2m.CreepTenuous.helpers.TestTagServiceFileSystem;
import com.zer0s2m.CreepTenuous.helpers.UtilsActionForFiles;
import com.zer0s2m.CreepTenuous.api.controllers.files.copy.data.DataCopyFile;
import com.zer0s2m.CreepTenuous.components.RootPath;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;
import com.zer0s2m.CreepTenuous.services.common.collectRootPath.impl.CollectRootPath;
import com.zer0s2m.CreepTenuous.services.files.copy.services.impl.ServiceCopyFile;

import com.zer0s2m.CreepTenuous.services.files.move.containers.ContainerMovingFiles;
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
        ServiceCopyFile.class,
        ServiceBuildDirectoryPath.class,
        CollectRootPath.class,
        RootPath.class,
})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceFileSystem
public class ServiceCopyFileTests {
    Logger logger = LogManager.getLogger(ServiceCopyFileTests.class);

    @Autowired
    private ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    private RootPath rootPath;

    @Autowired
    private ServiceCopyFile service;

    DataCopyFile RECORD_1 = new DataCopyFile(
            "testFile1.txt",
            "testFile1.txt",
            null,
            null,
            new ArrayList<>(),
            new ArrayList<>(),
            List.of("testFolder1"),
            List.of("testFolder1")
    );

    DataCopyFile RECORD_2 = new DataCopyFile(
            null,
            null,
            Arrays.asList("testFile1.txt", "testFile12txt"),
            Arrays.asList("testFile1.txt", "testFile12txt"),
            new ArrayList<>(),
            new ArrayList<>(),
            List.of("testFolder2"),
            List.of("testFolder2")
    );

    DataCopyFile INVALID_RECORD_NOT_IS_EXISTS_FILE = new DataCopyFile(
            "notIsExistsFile.txt",
            "notIsExistsFile.txt",
            null,
            null,
            new ArrayList<>(),
            new ArrayList<>(),
            List.of("testFolder1"),
            List.of("testFolder1")
    );

    DataCopyFile INVALID_RECORD_INVALID_PATH_DIRECTORY = new DataCopyFile(
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
    public void copyOneFile_Success() throws IOException {
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

        ContainerMovingFiles container = service.copy(RECORD_1.nameFile(), RECORD_1.parents(), RECORD_1.toParents());

        UtilsActionForFiles.deleteFileAndWriteLog(container.target(), logger);
        UtilsActionForFiles.deleteFileAndWriteLog(pathTestFile, logger);
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

        List<ContainerMovingFiles> containerList = service.copy(nameFiles, RECORD_2.parents(), RECORD_2.toParents());

        Assertions.assertTrue(Files.exists(pathTestFile1));
        Assertions.assertTrue(Files.exists(pathTestFile2));
        Assertions.assertTrue(Files.exists(containerList.get(0).target()));
        Assertions.assertTrue(Files.exists(containerList.get(1).target()));

        UtilsActionForFiles.deleteFileAndWriteLog(pathTestFile1, logger);
        UtilsActionForFiles.deleteFileAndWriteLog(pathTestFile2, logger);
        UtilsActionForFiles.deleteFileAndWriteLog(containerList.get(0).target(), logger);
        UtilsActionForFiles.deleteFileAndWriteLog(containerList.get(1).target(), logger);
        UtilsActionForFiles.deleteFileAndWriteLog(pathTestFolder, logger);
    }

    @Test
    public void moveFile_fail_notIsExistsFile() {
        Assertions.assertThrows(
                NoSuchFileException.class,
                () -> service.copy(
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
                () -> service.copy(
                        INVALID_RECORD_INVALID_PATH_DIRECTORY.nameFile(),
                        INVALID_RECORD_INVALID_PATH_DIRECTORY.parents(),
                        INVALID_RECORD_INVALID_PATH_DIRECTORY.toParents()
                )
        );
    }
}
