package com.zer0s2m.creeptenuous.services.system;

import com.zer0s2m.creeptenuous.common.http.ResponseUploadDirectoryApi;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceUploadDirectoryImpl;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagServiceFileSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;

@SpringBootTest(classes = {
        ServiceUploadDirectoryImpl.class
})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceFileSystem
public class ServiceUploadDirectoryTests {
    Logger logger = LogManager.getLogger(ServiceUploadDirectoryTests.class);

    @Autowired
    private ServiceUploadDirectoryImpl service;

    private final ServiceBuildDirectoryPath serviceBuildDirectoryPath = new ServiceBuildDirectoryPath();

    @Test
    public void uploadDirectory_success() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();

        String testFileZip = "test-zip.zip";
        File testFile = new File(classLoader.getResource(testFileZip).getFile());

        Path path = Files.copy(
                testFile.toPath(),
                Path.of(serviceBuildDirectoryPath.build(new ArrayList<>()), testFileZip)
        );

        Assertions.assertTrue(Files.exists(path));

        logger.info(String.format("Copy test file zip for tests: %s", path));

        InputStream targetStream = new FileInputStream(path.toFile());

        Path systemPath = service.getPath(new ArrayList<>());
        Path pathZipFile = service.getNewPathZipFile(systemPath, new MockMultipartFile(
                "directory",
                testFileZip,
                "application/zip",
                targetStream
        ));
        ResponseUploadDirectoryApi response = service.upload(systemPath, pathZipFile);

        targetStream.close();

        Assertions.assertTrue(response.success());
    }

    @Test
    public void uploadDirectory_fail_invalidPathDirectory() {
        Assertions.assertThrows(
                NoSuchFileException.class,
                () -> service.upload(
                        Path.of("invalid", "path", "directory"),
                        service.getNewPathZipFile(
                                Path.of("invalid", "path", "directory"),
                                new MockMultipartFile("test", "test".getBytes())
                        )
                )
        );
    }

}
