package com.zer0s2m.creeptenuous.services.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataCreateFile;
import com.zer0s2m.creeptenuous.common.data.DataCreateFileApi;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundTypeFileException;
import com.zer0s2m.creeptenuous.common.utils.UtilsFileSystem;
import com.zer0s2m.creeptenuous.services.system.helpers.UtilsActionForFiles;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceCreateFileImpl;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagServiceFileSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceFileSystem
public class ServiceCreateFileTests {

    Logger logger = LogManager.getLogger(ServiceCreateFileTests.class);

    private final ServiceCreateFile service = new ServiceCreateFileImpl();

    private final String nameTestFile1 = "tesFile_1";

    private final String nameTestFile2 = "tesFile_2";

    private final String nameTestFile3 = "tesFile_3";

    DataCreateFileApi RECORD_1 = new DataCreateFileApi(1, nameTestFile1, new ArrayList<>(), new ArrayList<>());

    DataCreateFileApi RECORD_2 = new DataCreateFileApi(2, nameTestFile2, new ArrayList<>(), new ArrayList<>());

    DataCreateFileApi RECORD_3 = new DataCreateFileApi(3, nameTestFile3, new ArrayList<>(), new ArrayList<>());

    DataCreateFileApi INVALID_RECORD_TYPE_FILE = new DataCreateFileApi(
            9999,
            "failFile",
            new ArrayList<>(),
            new ArrayList<>()
    );

    DataCreateFileApi INVALID_RECORD_PATH_DIRECTORY = new DataCreateFileApi(
            1,
            "failFile",
            Arrays.asList("fail", "path", "directory"),
            Arrays.asList("fail", "path", "directory")
    );

    @Test
    public void createFileType1_success() throws NotFoundTypeFileException, IOException {
        ContainerDataCreateFile container = service.create(
                RECORD_1.parents(),
                RECORD_1.fileName(),
                RECORD_1.typeFile()
        );

        Assertions.assertTrue(Files.exists(Path.of(UtilsFileSystem.clearSystemPathFile(
                container.systemPathFile()))));

        UtilsActionForFiles.deleteFileAndWriteLog(container.systemPathFile(), logger);
    }

    @Test
    public void createFileType2_success() throws NotFoundTypeFileException, IOException {
        ContainerDataCreateFile container = service.create(
                RECORD_2.parents(),
                RECORD_2.fileName(),
                RECORD_2.typeFile()
        );

        Assertions.assertTrue(Files.exists(Path.of(UtilsFileSystem.clearSystemPathFile(
                container.systemPathFile()))));

        UtilsActionForFiles.deleteFileAndWriteLog(container.systemPathFile(), logger);
    }

    @Test
    public void createFileType3_success() throws NotFoundTypeFileException, IOException {
        ContainerDataCreateFile container = service.create(
                RECORD_3.parents(),
                RECORD_3.fileName(),
                RECORD_3.typeFile()
        );

        Assertions.assertTrue(Files.exists(Path.of(UtilsFileSystem.clearSystemPathFile(
                container.systemPathFile()))));

        UtilsActionForFiles.deleteFileAndWriteLog(container.systemPathFile(), logger);
    }

    @Test
    public void createFile_fail_invalidTypeFile() {
        Assertions.assertThrows(
                NotFoundTypeFileException.class,
                () -> service.create(
                        INVALID_RECORD_TYPE_FILE.parents(),
                        INVALID_RECORD_TYPE_FILE.fileName(),
                        INVALID_RECORD_TYPE_FILE.typeFile()
                )
        );
    }

    @Test
    public void createFile_fail_invalidPathDirectory() {
        Assertions.assertThrows(
                NoSuchFileException.class,
                () -> service.create(
                        INVALID_RECORD_PATH_DIRECTORY.parents(),
                        INVALID_RECORD_PATH_DIRECTORY.fileName(),
                        INVALID_RECORD_PATH_DIRECTORY.typeFile()
                )
        );
    }

}
