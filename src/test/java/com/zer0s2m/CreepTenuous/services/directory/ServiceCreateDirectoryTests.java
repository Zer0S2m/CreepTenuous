package com.zer0s2m.CreepTenuous.services.directory;

import com.zer0s2m.CreepTenuous.components.RootPath;
import com.zer0s2m.CreepTenuous.helpers.UtilsActionForFiles;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;
import com.zer0s2m.CreepTenuous.services.common.collectRootPath.impl.CollectRootPath;
import com.zer0s2m.CreepTenuous.services.directory.create.services.impl.ServiceCreateDirectory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest(classes = {
        ServiceCreateDirectory.class,
        ServiceBuildDirectoryPath.class,
        CollectRootPath.class,
        RootPath.class,
})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ServiceCreateDirectoryTests {
    Logger logger = LogManager.getLogger(ServiceCreateDirectoryTests.class);

    @Autowired
    private ServiceCreateDirectory service;

    @Autowired
    private ServiceBuildDirectoryPath serviceBuildDirectoryPath;

    List<String> DIRECTORIES_1 = List.of("test_folder1");

    @Test
    public void createDirectory_success() throws IOException {
        service.create(new ArrayList<>(), DIRECTORIES_1.get(0));
        Path newFolder = Path.of(serviceBuildDirectoryPath.build(DIRECTORIES_1));
        Assertions.assertTrue(Files.exists(newFolder));
        UtilsActionForFiles.deleteFileAndWriteLog(newFolder, logger);
    }

    @Test
    public void createDirectory_fail_directoryExists() throws IOException {
        UtilsActionForFiles.createDirectories(DIRECTORIES_1, serviceBuildDirectoryPath, logger);
        Assertions.assertThrows(
                FileAlreadyExistsException.class,
                () -> service.create(
                        new ArrayList<>(),
                        DIRECTORIES_1.get(0)
                )
        );

        Path newFolder = Path.of(serviceBuildDirectoryPath.build(DIRECTORIES_1));
        UtilsActionForFiles.deleteFileAndWriteLog(newFolder, logger);
    }

    @Test
    public void createDirectory_fail_invalidPathDirectory() {
        Assertions.assertThrows(
                NoSuchFileException.class,
                () -> service.create(
                        Arrays.asList("invalid", "path", "directory"),
                        "testFolder"
                )
        );
    }
}
