package com.zer0s2m.creeptenuous.core.services;

import com.zer0s2m.creeptenuous.core.services.helpers.UtilsActionForFiles;
import com.zer0s2m.creeptenuous.core.services.impl.ServiceDownloadDirectoryImpl;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagServiceFileSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.util.FileSystemUtils;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceFileSystem
public class ServiceDownloadDirectoryTests {

    Logger logger = LogManager.getLogger(ServiceDownloadDirectoryTests.class);

    private final ServiceDownloadDirectory service = new ServiceDownloadDirectoryImpl();

    private final ServiceBuildDirectoryPath buildDirectoryPath = new ServiceBuildDirectoryPath();

    List<String> DIRECTORIES_1 = List.of("test_folder1");

    @Test
    public void downloadDirectory_success() throws IOException {
        UtilsActionForFiles.preparePreliminaryFilesForCopyDirectories(
                buildDirectoryPath,
                logger,
                DIRECTORIES_1,
                "test_file1.txt"
        );
        UtilsActionForFiles.preparePreliminaryFilesForCopyDirectories(
                buildDirectoryPath,
                logger,
                DIRECTORIES_1,
                "test_file2.txt"
        );

        Path sourceZipArchive = service.download(
                new ArrayList<>(),
                DIRECTORIES_1.get(0)
        );

        Assertions.assertTrue(Files.exists(sourceZipArchive));

        Path directoryTest = Path.of(buildDirectoryPath.build(DIRECTORIES_1));
        FileSystemUtils.deleteRecursively(directoryTest);
        Files.delete(sourceZipArchive);

        logger.info("Delete folder for tests: " + directoryTest);
    }

    @Test
    public void downloadDirectory_fail_invalidPathDirectory() {
        Assertions.assertThrows(
                NoSuchFileException.class,
                () -> service.download(
                        Arrays.asList("invalid", "path", "directory"),
                        DIRECTORIES_1.get(0)
                )
        );
    }

}
