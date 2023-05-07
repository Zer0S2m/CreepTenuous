package com.zer0s2m.CreepTenuous.services.directory;

import com.zer0s2m.CreepTenuous.components.RootPath;
import com.zer0s2m.CreepTenuous.helpers.TestTagServiceFileSystem;
import com.zer0s2m.CreepTenuous.helpers.UtilsActionForFiles;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;
import com.zer0s2m.CreepTenuous.services.common.collectRootPath.impl.CollectRootPath;
import com.zer0s2m.CreepTenuous.services.directory.copy.enums.MethodCopyDirectory;
import com.zer0s2m.CreepTenuous.services.directory.copy.services.impl.ServiceCopyDirectory;
import com.zer0s2m.CreepTenuous.utils.containers.ContainerInfoFileSystemObject;

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
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest(classes = {
        ServiceCopyDirectory.class,
        ServiceBuildDirectoryPath.class,
        CollectRootPath.class,
        RootPath.class
})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceFileSystem
public class ServiceCopyDirectoryTests {
    Logger logger = LogManager.getLogger(ServiceCopyDirectoryTests.class);

    @Autowired
    private ServiceCopyDirectory service;

    @Autowired
    private ServiceBuildDirectoryPath serviceBuildDirectoryPath;

    List<String> DIRECTORIES_1 = List.of("test_folder1");
    List<String> DIRECTORIES_2 = List.of("test_folder1", "test_folder2");
    List<String> DIRECTORIES_3 = List.of("test_folder3");

    @Test
    public void copyDirectoryFolder_success() throws IOException {
        UtilsActionForFiles.createDirectories(DIRECTORIES_3, serviceBuildDirectoryPath, logger);
        UtilsActionForFiles.preparePreliminaryFilesForCopyDirectories(
                serviceBuildDirectoryPath,
                logger,
                DIRECTORIES_1,
                "test_file1.txt"
        );
        UtilsActionForFiles.preparePreliminaryFilesForCopyDirectories(
                serviceBuildDirectoryPath,
                logger,
                DIRECTORIES_2,
                "test_file2.txt"
        );

        List<ContainerInfoFileSystemObject> dataCopy = service.copy(
                new ArrayList<>(),
                List.of(DIRECTORIES_3.get(0)),
                DIRECTORIES_1.get(0),
                MethodCopyDirectory.FOLDER.getMethod()
        );

        FileSystemUtils.deleteRecursively(Path.of(serviceBuildDirectoryPath.build(DIRECTORIES_1)));
        FileSystemUtils.deleteRecursively(Path.of(serviceBuildDirectoryPath.build(DIRECTORIES_3)));
    }

    @Test
    public void copyDirectoryContent_success() throws IOException {
        UtilsActionForFiles.createDirectories(DIRECTORIES_3, serviceBuildDirectoryPath, logger);
        UtilsActionForFiles.preparePreliminaryFilesForCopyDirectories(
                serviceBuildDirectoryPath,
                logger,
                DIRECTORIES_1,
                "test_file1.txt"
        );
        UtilsActionForFiles.preparePreliminaryFilesForCopyDirectories(
                serviceBuildDirectoryPath,
                logger,
                DIRECTORIES_2,
                "test_file2.txt"
        );

        List<ContainerInfoFileSystemObject> dataCopy = service.copy(
                new ArrayList<>(),
                List.of(DIRECTORIES_3.get(0)),
                DIRECTORIES_1.get(0),
                MethodCopyDirectory.CONTENT.getMethod()
        );

        FileSystemUtils.deleteRecursively(Path.of(serviceBuildDirectoryPath.build(DIRECTORIES_1)));
        FileSystemUtils.deleteRecursively(Path.of(serviceBuildDirectoryPath.build(DIRECTORIES_3)));
    }

    @Test
    public void copyDirectory_fail_invalidPathDirectory() {
        Assertions.assertThrows(
                NoSuchFileException.class,
                () -> service.copy(
                        Arrays.asList("invalid", "path", "directory"),
                        new ArrayList<>(),
                        "testFolder",
                        MethodCopyDirectory.CONTENT.getMethod()
                )
        );
    }

    @Test
    public void copyDirectory_fail_invalidToPathDirectory() {
        Assertions.assertThrows(
                NoSuchFileException.class,
                () -> service.copy(
                        new ArrayList<>(),
                        Arrays.asList("invalid", "path", "directory"),
                        "testFolder",
                        MethodCopyDirectory.CONTENT.getMethod()
                )
        );
    }
}
