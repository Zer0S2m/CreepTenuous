package com.zer0s2m.creeptenuous.services.system;

import com.zer0s2m.creeptenuous.common.http.ResponseObjectUploadFileApi;
import com.zer0s2m.creeptenuous.services.system.helpers.UtilsActionForFiles;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceUploadFileImpl;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagServiceFileSystem;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest(classes = {
        ServiceUploadFileImpl.class
})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceFileSystem
public class ServiceUploadFileTests {

    Logger logger = LogManager.getLogger(ServiceUploadFileTests.class);

    @Autowired
    private ServiceUploadFileImpl service;

    private final String nameTestFile1 = "test_image_1.jpeg";

    private final String nameTestFile2 = "test_image_2.jpeg";

    @Test
    public void uploadOneFile_success() throws IOException {
        InputStream targetStream = this.getClass().getResourceAsStream("/" + nameTestFile1);

        List<ResponseObjectUploadFileApi> containerList = service.upload(
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
        InputStream targetStream1 = this.getClass().getResourceAsStream("/" + nameTestFile1);
        InputStream targetStream2 = this.getClass().getResourceAsStream("/" + nameTestFile2);

        List<ResponseObjectUploadFileApi> containerList = service.upload(
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
