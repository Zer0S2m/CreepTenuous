package com.zer0s2m.CreepTenuous.services.files;

import com.zer0s2m.CreepTenuous.api.controllers.files.upload.http.DataUploadFile;
import com.zer0s2m.CreepTenuous.helpers.TestTagServiceFileSystem;
import com.zer0s2m.CreepTenuous.helpers.UtilsActionForFiles;
import com.zer0s2m.CreepTenuous.components.RootPath;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;
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
        ServiceBuildDirectoryPath.class,
        CollectRootPath.class,
        RootPath.class,
})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceFileSystem
public class ServiceUploadFileTests {
    Logger logger = LogManager.getLogger(ServiceUploadFileTests.class);

    @Autowired
    private ServiceUploadFile service;

    private final String nameTestFile1 = "test_image_1.jpeg";
    private final String nameTestFile2 = "test_image_2.jpeg";

    @Test
    public void uploadOneFile_success() throws IOException {
        File testFile = Path.of("src/main/resources/test/", nameTestFile1).toFile();
        InputStream targetStream = new FileInputStream(testFile);

        List<DataUploadFile> containerList = service.upload(
                List.of(getMockFile(nameTestFile1, targetStream)),
                new ArrayList<>()
        );

        targetStream.close();

        Assertions.assertTrue(Files.exists(containerList.get(0).systemPath()));
        UtilsActionForFiles.deleteFileAndWriteLog(containerList.get(0).systemPath(), logger);
        Assertions.assertFalse(Files.exists(containerList.get(0).systemPath()));
    }

    @Test
    public void uploadMoreOneFile_success() throws IOException {
        File testFile1 = Path.of("src/main/resources/test/" + nameTestFile1).toFile();
        File testFile2 = Path.of("src/main/resources/test/" + nameTestFile2).toFile();
        InputStream targetStream1 = new FileInputStream(testFile1);
        InputStream targetStream2 = new FileInputStream(testFile2);

        List<DataUploadFile> containerList = service.upload(
                Arrays.asList(
                        getMockFile(nameTestFile1, targetStream1),
                        getMockFile(nameTestFile2, targetStream2)
                ),
                new ArrayList<>()
        );

        targetStream1.close();
        targetStream2.close();

        Assertions.assertTrue(Files.exists(containerList.get(0).systemPath()));
        Assertions.assertTrue(Files.exists(containerList.get(1).systemPath()));

        UtilsActionForFiles.deleteFileAndWriteLog(containerList.get(0).systemPath(), logger);
        UtilsActionForFiles.deleteFileAndWriteLog(containerList.get(1).systemPath(), logger);

        Assertions.assertFalse(Files.exists(containerList.get(0).systemPath()));
        Assertions.assertFalse(Files.exists(containerList.get(1).systemPath()));
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
