package com.zer0s2m.creeptenuous.core.services;

import com.zer0s2m.creeptenuous.core.services.helpers.UtilsActionForFiles;
import com.zer0s2m.creeptenuous.core.services.impl.ServiceDeleteDirectoryImpl;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagServiceFileSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceFileSystem
public class ServiceDeleteDirectoryTests {

    Logger logger = LogManager.getLogger(ServiceDeleteDirectoryTests.class);

    private final ServiceDeleteDirectory service = new ServiceDeleteDirectoryImpl();

    private final ServiceBuildDirectoryPath serviceBuildDirectoryPath = new ServiceBuildDirectoryPath();

    List<String> DIRECTORIES_1 = List.of("test_folder1");

    @Test
    public void deleteDirectory_success() throws IOException {
        UtilsActionForFiles.createDirectories(DIRECTORIES_1, serviceBuildDirectoryPath, logger);
        Path deletedFolder = Path.of(serviceBuildDirectoryPath.build(DIRECTORIES_1));
        service.delete(new ArrayList<>(), DIRECTORIES_1.get(0));
        Assertions.assertFalse(Files.exists(deletedFolder));
    }

    @Test
    public void deleteDirectory_fail_objectIsFile() throws Exception {
        final Path pathFile = Path.of(Path.of(serviceBuildDirectoryPath.build(), "testFile.txt").toUri());
        Files.createFile(pathFile);

        Assertions.assertDoesNotThrow(
                () -> service.delete(new ArrayList<>(), "testFile.txt")
        );

        Files.deleteIfExists(pathFile);
    }

    @Test
    public void deleteDirectoryPath_success() throws Exception {
        final Path pathFolder = Path.of(Path.of(serviceBuildDirectoryPath.build(), "directory").toUri());
        Files.createDirectory(pathFolder);

        Assertions.assertDoesNotThrow(
                () -> service.delete(pathFolder)
        );

        Assertions.assertFalse(Files.exists(pathFolder));
    }

    @Test
    public void deleteDirectoryPath_fail_objectIsFile() throws Exception {
        final Path pathFile = Path.of(Path.of(serviceBuildDirectoryPath.build(), "testFile.txt").toUri());
        Files.createFile(pathFile);

        Assertions.assertDoesNotThrow(
                () -> service.delete(pathFile)
        );

        Files.deleteIfExists(pathFile);
    }

    @Test
    public void deleteDirectory_fail_invalidPathDirectory() {
        Assertions.assertThrows(
                NoSuchFileException.class,
                () -> service.delete(
                        Arrays.asList("invalid", "path", "directory"),
                        "testFolder"
                )
        );
    }

}
