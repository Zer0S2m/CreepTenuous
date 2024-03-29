package com.zer0s2m.creeptenuous.services.system;

import com.zer0s2m.creeptenuous.common.components.RootPath;
import com.zer0s2m.creeptenuous.services.system.helpers.UtilsActionForFiles;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
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
import java.util.Arrays;
import java.util.List;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceFileSystem
public class ServiceBuildDirectoryPathTests {

    Logger logger = LogManager.getLogger(ServiceBuildDirectoryPathTests.class);

    private final ServiceBuildDirectoryPath serviceBuildDirectoryPath = new ServiceBuildDirectoryPath();

    private final RootPath rootPath = new RootPath();

    List<String> DIRECTORIES_1 = Arrays.asList("test_folder1", "test_folder2");

    List<String> DIRECTORIES_2 = Arrays.asList("test_folder1", "test_folder3");

    List<String> DIRECTORIES_3 = Arrays.asList("test_folder1", "test_folder3", "test_folder4");

    List<String> INVALID_DIRECTORIES = Arrays.asList("invalid", "path", "directory");

    @Test
    public void build_success() throws IOException {
        UtilsActionForFiles.createDirectories(DIRECTORIES_1, serviceBuildDirectoryPath, logger);
        UtilsActionForFiles.createDirectories(DIRECTORIES_2, serviceBuildDirectoryPath, logger);
        UtilsActionForFiles.createDirectories(DIRECTORIES_3, serviceBuildDirectoryPath, logger);

        Path path1 = Path.of(serviceBuildDirectoryPath.build(DIRECTORIES_1));
        Path path2 = Path.of(serviceBuildDirectoryPath.build(DIRECTORIES_2));
        Path path3 = Path.of(serviceBuildDirectoryPath.build(DIRECTORIES_3));

        Assertions.assertTrue(Files.exists(path1));
        Assertions.assertTrue(Files.exists(path2));
        Assertions.assertTrue(Files.exists(path3));

        UtilsActionForFiles.deleteFileAndWriteLog(path3, logger);
        UtilsActionForFiles.deleteFileAndWriteLog(path2, logger);
        UtilsActionForFiles.deleteFileAndWriteLog(path1, logger);
        UtilsActionForFiles.deleteFileAndWriteLog(Path.of(rootPath.getRootPath(), DIRECTORIES_1.get(0)), logger);
    }

    @Test
    public void build_fail_invalidDirectory() {
        Assertions.assertThrows(
                NoSuchFileException.class,
                () -> serviceBuildDirectoryPath.build(INVALID_DIRECTORIES)
        );
    }

}
