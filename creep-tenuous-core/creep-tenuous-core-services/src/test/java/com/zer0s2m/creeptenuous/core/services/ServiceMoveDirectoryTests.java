package com.zer0s2m.creeptenuous.core.services;

import com.zer0s2m.creeptenuous.common.enums.MethodCopyDirectory;
import com.zer0s2m.creeptenuous.core.services.helpers.UtilsActionForFiles;
import com.zer0s2m.creeptenuous.core.services.impl.ServiceMoveDirectoryImpl;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagServiceFileSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceFileSystem
public class ServiceMoveDirectoryTests {

    Logger logger = LogManager.getLogger(ServiceMoveDirectoryTests.class);

    private final ServiceMoveDirectory service = new ServiceMoveDirectoryImpl();

    private final ServiceBuildDirectoryPath serviceBuildDirectoryPath = new ServiceBuildDirectoryPath();

    List<String> DIRECTORIES_1 = List.of("test_folder1");

    List<String> DIRECTORIES_2 = List.of("test_folder1", "test_folder2");

    List<String> DIRECTORIES_3 = List.of("test_folder3");

    @Test
    public void moveDirectoryFolder_success() throws IOException {
        UtilsActionForFiles.createDirectories(DIRECTORIES_3, serviceBuildDirectoryPath, logger);
        Path pathTestFile1 =  UtilsActionForFiles.preparePreliminaryFilesForCopyDirectories(
                serviceBuildDirectoryPath,
                logger,
                DIRECTORIES_1,
                "test_file1.txt"
        );
        Path pathTestFile2 = UtilsActionForFiles.preparePreliminaryFilesForCopyDirectories(
                serviceBuildDirectoryPath,
                logger,
                DIRECTORIES_2,
                "test_file2.txt"
        );

        service.move(
                new ArrayList<>(),
                List.of(DIRECTORIES_3.get(0)),
                DIRECTORIES_1.get(0),
                MethodCopyDirectory.FOLDER.getMethod()
        );

        Path newPathTestFile1 = Path.of(
                serviceBuildDirectoryPath.build(DIRECTORIES_3),
                DIRECTORIES_1.get(0),
                "test_file1.txt"
        );
        Path newPathTestFile2 = Path.of(
                serviceBuildDirectoryPath.build(DIRECTORIES_3),
                DIRECTORIES_1.get(0),
                "test_folder2",
                "test_file2.txt"
        );
        Assertions.assertTrue(Files.exists(newPathTestFile1));
        Assertions.assertTrue(Files.exists(newPathTestFile2));
        Assertions.assertFalse(Files.exists(pathTestFile1));
        Assertions.assertFalse(Files.exists(pathTestFile2));

        FileSystemUtils.deleteRecursively(Path.of(serviceBuildDirectoryPath.build(DIRECTORIES_3)));
    }

    @Test
    public void moveDirectoryContent_success() throws IOException {
        UtilsActionForFiles.createDirectories(DIRECTORIES_3, serviceBuildDirectoryPath, logger);
        Path pathTestFile1 =  UtilsActionForFiles.preparePreliminaryFilesForCopyDirectories(
                serviceBuildDirectoryPath,
                logger,
                DIRECTORIES_1,
                "test_file1.txt"
        );
        Path pathTestFile2 = UtilsActionForFiles.preparePreliminaryFilesForCopyDirectories(
                serviceBuildDirectoryPath,
                logger,
                DIRECTORIES_2,
                "test_file2.txt"
        );

        service.move(
                new ArrayList<>(),
                List.of(DIRECTORIES_3.get(0)),
                DIRECTORIES_1.get(0),
                MethodCopyDirectory.CONTENT.getMethod()
        );

        Path newPathTestFile1 = Path.of(
                serviceBuildDirectoryPath.build(DIRECTORIES_3),
                "test_file1.txt"
        );
        Path newPathTestFile2 = Path.of(
                serviceBuildDirectoryPath.build(DIRECTORIES_3),
                "test_folder2",
                "test_file2.txt"
        );
        Assertions.assertTrue(Files.exists(newPathTestFile1));
        Assertions.assertTrue(Files.exists(newPathTestFile2));
        Assertions.assertFalse(Files.exists(pathTestFile1));
        Assertions.assertFalse(Files.exists(pathTestFile2));

        FileSystemUtils.deleteRecursively(Path.of(serviceBuildDirectoryPath.build(DIRECTORIES_3)));
    }

}
