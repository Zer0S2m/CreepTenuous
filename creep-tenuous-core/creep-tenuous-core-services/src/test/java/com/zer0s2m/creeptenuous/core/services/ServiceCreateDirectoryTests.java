package com.zer0s2m.creeptenuous.core.services;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataCreateDirectory;
import com.zer0s2m.creeptenuous.core.services.helpers.UtilsActionForFiles;
import com.zer0s2m.creeptenuous.core.services.impl.ServiceCreateDirectoryImpl;
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
import java.util.ArrayList;
import java.util.Arrays;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceFileSystem
public class ServiceCreateDirectoryTests {

    Logger logger = LogManager.getLogger(ServiceCreateDirectoryTests.class);

    private final ServiceCreateDirectory service = new ServiceCreateDirectoryImpl();

    @Test
    public void createDirectory_success() throws IOException {
        ContainerDataCreateDirectory dataCreated = service.create(new ArrayList<>(), "test");
        Assertions.assertTrue(Files.exists(dataCreated.pathDirectory()));
        UtilsActionForFiles.deleteFileAndWriteLog(dataCreated.pathDirectory(), logger);
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
