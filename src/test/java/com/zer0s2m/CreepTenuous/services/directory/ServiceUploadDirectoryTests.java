package com.zer0s2m.CreepTenuous.services.directory;

import com.zer0s2m.CreepTenuous.api.controllers.directory.upload.http.ResponseUploadDirectory;
import com.zer0s2m.CreepTenuous.components.RootPath;
import com.zer0s2m.CreepTenuous.helpers.TestTagServiceFileSystem;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;
import com.zer0s2m.CreepTenuous.services.common.collectRootPath.impl.CollectRootPath;
import com.zer0s2m.CreepTenuous.services.directory.upload.containers.ContainerDataUploadFile;
import com.zer0s2m.CreepTenuous.services.directory.upload.services.impl.ServiceUploadDirectory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@SpringBootTest(classes = {
        ServiceUploadDirectory.class,
        ServiceBuildDirectoryPath.class,
        CollectRootPath.class,
        RootPath.class
})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceFileSystem
public class ServiceUploadDirectoryTests {
    Logger logger = LogManager.getLogger(ServiceUploadDirectoryTests.class);

    @Autowired
    private ServiceUploadDirectory service;

    @Autowired
    private ServiceBuildDirectoryPath serviceBuildDirectoryPath;

    @Test
    public void uploadDirectory_success() throws IOException, InterruptedException, ExecutionException {
        String testFileZip = "test-zip.zip";
        File testFile = new File("src/main/resources/test/" + testFileZip);

        Path path = Files.copy(
                testFile.toPath(),
                Path.of(serviceBuildDirectoryPath.build(new ArrayList<>()), testFileZip)
        );

        Assertions.assertTrue(Files.exists(path));

        logger.info(String.format("Copy test file zip for tests: %s", path));

        InputStream targetStream = new FileInputStream(path.toFile());

        CompletableFuture<ResponseUploadDirectory> response = service.upload(new ArrayList<>(), new MockMultipartFile(
                "directory",
                testFileZip,
                "application/zip",
                targetStream
        ));

        targetStream.close();

        Assertions.assertTrue(response.get().success());

        List<ContainerDataUploadFile> container = response.get().data();

        Assertions.assertTrue(Files.exists(container.get(0).systemPath()));
        Assertions.assertTrue(Files.exists(container.get(1).systemPath()));
        Assertions.assertTrue(Files.exists(container.get(2).systemPath()));
        Assertions.assertTrue(Files.exists(container.get(3).systemPath()));
        Assertions.assertTrue(Files.exists(container.get(4).systemPath()));
        Assertions.assertTrue(Files.exists(container.get(5).systemPath()));

        container.forEach((obj) -> {
            if (obj.isDirectory()) {
                try {
                    FileSystemUtils.deleteRecursively(obj.systemPath());
                    logger.info(String.format("Delete folder for tests: %s", obj.systemPath()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void uploadDirectory_fail_invalidPathDirectory() {
        Assertions.assertThrows(
                NoSuchFileException.class,
                () -> service.upload(
                        Arrays.asList("invalid", "path", "directory"),
                        new MockMultipartFile("test", "test".getBytes())
                )
        );
    }
}
