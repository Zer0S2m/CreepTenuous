package com.zer0s2m.CreepTenuous.services.directory;

import com.zer0s2m.CreepTenuous.api.controllers.directory.upload.http.ResponseUploadDirectory;
import com.zer0s2m.CreepTenuous.components.RootPath;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;
import com.zer0s2m.CreepTenuous.services.common.collectRootPath.impl.CollectRootPath;
import com.zer0s2m.CreepTenuous.services.directory.upload.services.impl.ServiceUploadDirectory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
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

@SpringBootTest(classes = {
        ServiceUploadDirectory.class,
        ServiceBuildDirectoryPath.class,
        CollectRootPath.class,
        RootPath.class
})
public class ServiceUploadDirectoryTests {
    Logger logger = LogManager.getLogger(ServiceUploadDirectoryTests.class);

    @Autowired
    private ServiceUploadDirectory service;

    @Autowired
    private ServiceBuildDirectoryPath serviceBuildDirectoryPath;

    @Test
    public void uploadDirectory_success() throws IOException, InterruptedException {
        String testFileZip = "test-zip.zip";
        File testFile = new File("src/main/resources/test/" + testFileZip);

        Path path = Files.copy(
                testFile.toPath(),
                Path.of(serviceBuildDirectoryPath.build(new ArrayList<>()), testFileZip)
        );

        Assertions.assertTrue(Files.exists(path));

        logger.info(String.format("Copy test file zip for tests: %s", path));

        InputStream targetStream = new FileInputStream(path.toFile());

        ResponseUploadDirectory response = service.upload(new ArrayList<>(), new MockMultipartFile(
                "directory",
                testFileZip,
                "application/zip",
                targetStream
        ));

        targetStream.close();

        String testPath = serviceBuildDirectoryPath.build(new ArrayList<>());

        Assertions.assertTrue(response.success());

        Thread.sleep(500);

        Assertions.assertTrue(Files.exists(Path.of(testPath, "folder_1", "test_image_1.jpeg")));
        Assertions.assertTrue(Files.exists(Path.of(testPath, "folder_1", "folder_2", "test_image_1.jpeg")));
        Assertions.assertTrue(Files.exists(Path.of(testPath, "folder_1", "folder_2", "test_image_2.jpeg")));
        Assertions.assertTrue(Files.exists(Path.of(testPath, "folder_1", "folder_2", "test-file-1.txt")));
        Assertions.assertTrue(Files.exists(Path.of(testPath, "folder_1", "folder_3", "test-file-1.txt")));
        Assertions.assertTrue(Files.exists(Path.of(testPath, "folder_4", "test_image_1.jpeg")));

        Path pathTestFolder1 = Path.of(serviceBuildDirectoryPath.build(List.of("folder_1")));
        Path pathTestFolder2 = Path.of(serviceBuildDirectoryPath.build(List.of("folder_4")));
        FileSystemUtils.deleteRecursively(pathTestFolder1);
        FileSystemUtils.deleteRecursively(pathTestFolder2);

        logger.info(String.format("Delete folders for tests: %s; %s", pathTestFolder1, pathTestFolder2));
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
