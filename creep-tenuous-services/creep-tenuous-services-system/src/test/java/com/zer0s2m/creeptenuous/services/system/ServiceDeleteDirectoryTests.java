package com.zer0s2m.creeptenuous.services.system;

import com.zer0s2m.creeptenuous.common.components.RootPath;
import com.zer0s2m.creeptenuous.services.system.helpers.UtilsActionForFiles;
import com.zer0s2m.creeptenuous.services.system.core.CollectRootPathImpl;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceDeleteDirectoryImpl;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagServiceFileSystem;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest(classes = {
        ServiceDeleteDirectoryImpl.class,
        ServiceBuildDirectoryPath.class,
        CollectRootPathImpl.class,
        RootPath.class,
})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceFileSystem
public class ServiceDeleteDirectoryTests {
    Logger logger = LogManager.getLogger(ServiceDeleteDirectoryTests.class);

    @Autowired
    private ServiceDeleteDirectoryImpl service;

    @Autowired
    private ServiceBuildDirectoryPath serviceBuildDirectoryPath;

    List<String> DIRECTORIES_1 = List.of("test_folder1");

    @Test
    public void deleteDirectory_success() throws IOException {
        UtilsActionForFiles.createDirectories(DIRECTORIES_1, serviceBuildDirectoryPath, logger);
        Path deletedFolder = Path.of(serviceBuildDirectoryPath.build(DIRECTORIES_1));
        service.delete(new ArrayList<>(), DIRECTORIES_1.get(0));
        Assertions.assertFalse(Files.exists(deletedFolder));
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
