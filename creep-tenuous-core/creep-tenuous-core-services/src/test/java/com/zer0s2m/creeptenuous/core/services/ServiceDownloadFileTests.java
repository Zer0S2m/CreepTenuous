package com.zer0s2m.creeptenuous.core.services;

import com.zer0s2m.creeptenuous.common.components.RootPath;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataDownloadFile;
import com.zer0s2m.creeptenuous.core.services.helpers.UtilsActionForFiles;
import com.zer0s2m.creeptenuous.core.services.impl.ServiceDownloadFileImpl;
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
import java.nio.file.Path;
import java.util.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceFileSystem
public class ServiceDownloadFileTests {
    Logger logger = LogManager.getLogger(ServiceDownloadFileTests.class);

    private final ServiceDownloadFile service = new ServiceDownloadFileImpl();

    private final RootPath rootPath = new RootPath();

    private final String failNameTestFile = "fail_name_test_file.fail_extension";

    @Test
    public void downloadFileAndCompareHeaders_success() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();

        String nameTestFile1 = "test_image_1.jpeg";

        Path sourcePath = Path.of(Objects.requireNonNull(classLoader.getResource(nameTestFile1)).getPath());
        Path targetPath = Path.of(rootPath.getRootPath(), nameTestFile1);
        Files.copy(sourcePath, targetPath);

        Assertions.assertTrue(Files.exists(targetPath));

        logger.info("Copy file: " + targetPath);

        ContainerDataDownloadFile<Path, String> dataFile = service.download(
                new ArrayList<>(),
                nameTestFile1
        );

        Assertions.assertEquals(dataFile.filename(), nameTestFile1);

        UtilsActionForFiles.deleteFileAndWriteLog(targetPath, logger);
        Assertions.assertFalse(Files.exists(targetPath));
    }

    @Test
    public void moveFile_fail_notIsExistsFile() {
        Assertions.assertThrows(
                NoSuchFileException.class,
                () -> service.download(
                        List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString()),
                        failNameTestFile
                )
        );
    }

    @Test
    public void moveFile_fail_invalidPathDirectory() {
        Assertions.assertThrows(
                NoSuchFileException.class,
                () -> service.download(
                        Arrays.asList("invalid", "path", "directory"),
                        failNameTestFile
                )
        );
    }

}
