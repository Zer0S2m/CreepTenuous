package com.zer0s2m.CreepTenuous.services.files;

import com.zer0s2m.CreepTenuous.helpers.UtilsActionForFiles;
import com.zer0s2m.CreepTenuous.components.RootPath;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.BuildDirectoryPath;
import com.zer0s2m.CreepTenuous.services.common.collectRootPath.impl.CollectRootPath;
import com.zer0s2m.CreepTenuous.services.files.upload.services.impl.ServiceUploadFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.opc.ContentTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest(classes = {
        ServiceUploadFile.class,
        BuildDirectoryPath.class,
        CollectRootPath.class,
        RootPath.class,
})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ServiceUploadFileTests {
    Logger logger = LogManager.getLogger(ServiceUploadFileTests.class);

    @Autowired
    private ServiceUploadFile service;

    @Autowired
    private BuildDirectoryPath buildDirectoryPath;

    private final String nameTestFile1 = "test_image_1.jpeg";
    private final String nameTestFile2 = "test_image_2.jpeg";

    @Test
    public void uploadOneFile_success() throws IOException {
        File testFile = new File("src/main/resources/test/" + nameTestFile1);
        InputStream targetStream = new FileInputStream(testFile);

        Path pathTestUploadFile = UtilsActionForFiles.preparePreliminaryFiles(
                nameTestFile1, new ArrayList<>(), logger, buildDirectoryPath
        );

        service.upload(
                List.of(getMockFile(nameTestFile1, targetStream)),
                new ArrayList<>()
        );

        targetStream.close();

        Assertions.assertTrue(Files.exists(pathTestUploadFile));

        UtilsActionForFiles.deleteFileAndWriteLog(pathTestUploadFile, logger);

        Assertions.assertFalse(Files.exists(pathTestUploadFile));
    }

    @Test
    public void uploadMoreOneFile_success() throws IOException {
        File testFile1 = new File("src/main/resources/test/" + nameTestFile1);
        File testFile2 = new File("src/main/resources/test/" + nameTestFile1);
        InputStream targetStream1 = new FileInputStream(testFile1);
        InputStream targetStream2 = new FileInputStream(testFile2);

        Path pathTestUploadFile1 = UtilsActionForFiles.preparePreliminaryFiles(
                nameTestFile1, new ArrayList<>(), logger, buildDirectoryPath
        );
        Path pathTestUploadFile2 = UtilsActionForFiles.preparePreliminaryFiles(
                nameTestFile1, new ArrayList<>(), logger, buildDirectoryPath
        );

        service.upload(
                Arrays.asList(
                        getMockFile(nameTestFile1, targetStream1),
                        getMockFile(nameTestFile2, targetStream2)
                ),
                new ArrayList<>()
        );

        targetStream1.close();
        targetStream2.close();

        Assertions.assertTrue(Files.exists(pathTestUploadFile1));
        Assertions.assertTrue(Files.exists(pathTestUploadFile2));

        UtilsActionForFiles.deleteFileAndWriteLog(pathTestUploadFile1, logger);
        UtilsActionForFiles.deleteFileAndWriteLog(pathTestUploadFile2, logger);

        Assertions.assertFalse(Files.exists(pathTestUploadFile1));
        Assertions.assertFalse(Files.exists(pathTestUploadFile2));
    }

    protected MockMultipartFile getMockFile(String nameFile, InputStream stream) throws IOException {
        return new MockMultipartFile(
                "files",
                nameFile,
                ContentTypes.IMAGE_JPEG,
                stream
        );
    }

    @Test
    public void uploadFile_fail_invalidPathDirectory() {
        Assertions.assertThrows(
                NoSuchFileException.class,
                () -> service.upload(
                        new ArrayList<>(),
                        Arrays.asList("invalid", "path", "directory")
                )
        );
    }
}
