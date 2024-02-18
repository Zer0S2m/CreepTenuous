package com.zer0s2m.creeptenuous.core.services;

import com.zer0s2m.creeptenuous.common.http.ResponseUploadDirectoryApi;
import com.zer0s2m.creeptenuous.core.services.impl.ServiceUploadDirectoryImpl;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagServiceFileSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceFileSystem
public class ServiceUploadDirectoryTests {
    Logger logger = LogManager.getLogger(ServiceUploadDirectoryTests.class);

    private final ServiceUploadDirectory service = new ServiceUploadDirectoryImpl();

    private final ServiceBuildDirectoryPath serviceBuildDirectoryPath = new ServiceBuildDirectoryPath();

    @Test
    public void uploadDirectory_success() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();

        String testFileZip = "test-zip.zip";
        File testFile = new File(Objects.requireNonNull(classLoader.getResource(testFileZip)).getFile());

        Path path = Files.copy(
                testFile.toPath(),
                Path.of(serviceBuildDirectoryPath.build(new ArrayList<>()), testFileZip)
        );

        Assertions.assertTrue(Files.exists(path));

        logger.info(String.format("Copy test file zip for tests: %s", path));

        Path systemPath = service.getPath(new ArrayList<>());
        ResponseUploadDirectoryApi response = service.upload(systemPath, path);

        Assertions.assertTrue(response.success());
    }

}
