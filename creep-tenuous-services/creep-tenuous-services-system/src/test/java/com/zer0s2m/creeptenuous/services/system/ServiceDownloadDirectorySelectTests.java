package com.zer0s2m.creeptenuous.services.system;

import com.zer0s2m.creeptenuous.common.data.DataDownloadDirectorySelectPartApi;
import com.zer0s2m.creeptenuous.services.system.core.CollectRootPathImpl;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import com.zer0s2m.creeptenuous.services.system.helpers.UtilsActionForFiles;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceDownloadDirectorySelectImpl;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagServiceFileSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest(classes = {
        ServiceDownloadDirectorySelectImpl.class,
        ServiceBuildDirectoryPath.class,
        CollectRootPathImpl.class
})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceFileSystem
public class ServiceDownloadDirectorySelectTests {

    Logger logger = LogManager.getLogger(ServiceDownloadDirectorySelectTests.class);

    @Autowired
    private ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    private ServiceDownloadDirectorySelectImpl service;

    List<String> DIRECTORIES_1 = List.of("test_folder1");

    List<String> DIRECTORIES_2 = List.of("test_folder2");

    @Test
    public void downloadDirectorySelect_success() throws IOException {
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
        UtilsActionForFiles.preparePreliminaryFilesForCopyDirectories(
                buildDirectoryPath,
                logger,
                DIRECTORIES_2,
                "test_file3.txt"
        );

        service.setMap(null);
        Path sourceZipArchive = service.download(
                Arrays.asList(
                        new DataDownloadDirectorySelectPartApi(
                                new ArrayList<>(),
                                new ArrayList<>(),
                                DIRECTORIES_1.get(0),
                                DIRECTORIES_1.get(0)
                        ),
                        new DataDownloadDirectorySelectPartApi(
                                DIRECTORIES_2,
                                DIRECTORIES_2,
                                "test_file3.txt",
                                "test_file3.txt"
                        )
                )
        );

        Assertions.assertTrue(Files.exists(sourceZipArchive));

        Path directoryTest1 = Path.of(buildDirectoryPath.build(DIRECTORIES_1));
        Path directoryTest2 = Path.of(buildDirectoryPath.build(DIRECTORIES_2));
        FileSystemUtils.deleteRecursively(directoryTest1);
        FileSystemUtils.deleteRecursively(directoryTest2);
        Files.delete(sourceZipArchive);
    }

    @Test
    public void downloadDirectorySelect_fail_invalidPathDirectory() {
        Assertions.assertThrows(
                RuntimeException.class,
                () -> service.download(
                        List.of(new DataDownloadDirectorySelectPartApi(
                                Arrays.asList("invalid", "path", "directory"),
                                Arrays.asList("invalid", "path", "directory"),
                                DIRECTORIES_1.get(0),
                                DIRECTORIES_1.get(0)
                        ))
                )
        );
    }

}
