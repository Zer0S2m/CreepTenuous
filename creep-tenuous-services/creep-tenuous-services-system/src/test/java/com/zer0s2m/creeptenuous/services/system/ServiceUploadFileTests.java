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
import org.springframework.mock.web.MockMultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceFileSystem
public class ServiceUploadFileTests {

    Logger logger = LogManager.getLogger(ServiceUploadFileTests.class);

    private final ServiceUploadFile service = new ServiceUploadFileImpl();

    private final String nameTestFile1 = "test_image_1.jpeg";

    private final String nameTestFile2 = "test_image_2.jpeg";

    @Test
    public void uploadOneFile_success() throws IOException {
        InputStream targetStream = this.getClass().getResourceAsStream("/" + nameTestFile1);

        assert targetStream != null;

        Path source = Path.of(System.getProperty("java.io.tmpdir"), nameTestFile1);
        Files.copy(targetStream, source);

        Map<Path, String> dataFiles = new HashMap<>();
        dataFiles.put(Path.of(source.toString()), nameTestFile1);

        List<ResponseObjectUploadFileApi> containerList = service.upload(dataFiles, new ArrayList<>());

        targetStream.close();

        Assertions.assertTrue(Files.exists(containerList.get(0).systemPath()));
        UtilsActionForFiles.deleteFileAndWriteLog(containerList.get(0).systemPath(), logger);
        Assertions.assertFalse(Files.exists(containerList.get(0).systemPath()));
        Files.deleteIfExists(source);
    }

    @Test
    public void uploadMoreOneFile_success() throws IOException {
        InputStream targetStream1 = this.getClass().getResourceAsStream("/" + nameTestFile1);
        InputStream targetStream2 = this.getClass().getResourceAsStream("/" + nameTestFile2);

        assert targetStream1 != null;
        assert targetStream2 != null;

        Path source1 = Path.of(System.getProperty("java.io.tmpdir"), nameTestFile1);
        Path source2 = Path.of(System.getProperty("java.io.tmpdir"), nameTestFile2);
        Files.copy(targetStream1, source1);
        Files.copy(targetStream2, source2);

        Map<Path, String> dataFiles = new HashMap<>();
        dataFiles.put(Path.of(source1.toString()), nameTestFile1);
        dataFiles.put(Path.of(source2.toString()), nameTestFile2);

        List<ResponseObjectUploadFileApi> containerList = service.upload(dataFiles, new ArrayList<>());

        targetStream1.close();
        targetStream2.close();

        Assertions.assertTrue(Files.exists(containerList.get(0).systemPath()));
        Assertions.assertTrue(Files.exists(containerList.get(1).systemPath()));

        UtilsActionForFiles.deleteFileAndWriteLog(containerList.get(0).systemPath(), logger);
        UtilsActionForFiles.deleteFileAndWriteLog(containerList.get(1).systemPath(), logger);

        Assertions.assertFalse(Files.exists(containerList.get(0).systemPath()));
        Assertions.assertFalse(Files.exists(containerList.get(1).systemPath()));

        Files.deleteIfExists(source1);
        Files.deleteIfExists(source2);
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
                        new HashMap<>(),
                        Arrays.asList("invalid", "path", "directory")
                )
        );
    }

}
