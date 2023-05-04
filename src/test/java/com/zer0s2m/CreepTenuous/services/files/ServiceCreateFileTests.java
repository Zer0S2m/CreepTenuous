package com.zer0s2m.CreepTenuous.services.files;

import com.zer0s2m.CreepTenuous.helpers.TestTagServiceFileSystem;
import com.zer0s2m.CreepTenuous.helpers.UtilsActionForFiles;
import com.zer0s2m.CreepTenuous.api.controllers.files.create.data.DataCreateFile;
import com.zer0s2m.CreepTenuous.components.RootPath;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;
import com.zer0s2m.CreepTenuous.services.common.collectRootPath.impl.CollectRootPath;
import com.zer0s2m.CreepTenuous.services.files.create.exceptions.NotFoundTypeFileException;
import com.zer0s2m.CreepTenuous.services.files.create.services.impl.ServiceCreateFile;
import com.zer0s2m.CreepTenuous.services.core.TypeFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;

@SpringBootTest(classes = {
        ServiceCreateFile.class,
        ServiceBuildDirectoryPath.class,
        CollectRootPath.class,
        RootPath.class,
})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceFileSystem
public class ServiceCreateFileTests {
    Logger logger = LogManager.getLogger(ServiceCreateFileTests.class);

    @Autowired
    private ServiceCreateFile service;

    @Autowired
    private ServiceBuildDirectoryPath buildDirectoryPath;

    private final String nameTestFile1 = "tesFile_1";
    private final String nameTestFile2 = "tesFile_2";
    private final String nameTestFile3 = "tesFile_3";
    private final String nameTestFile4 = "tesFile_4";

    DataCreateFile RECORD_1 = new DataCreateFile(1, nameTestFile1, new ArrayList<>(), new ArrayList<>());
    DataCreateFile RECORD_2 = new DataCreateFile(2, nameTestFile2, new ArrayList<>(), new ArrayList<>());
    DataCreateFile RECORD_3 = new DataCreateFile(3, nameTestFile3, new ArrayList<>(), new ArrayList<>());
    DataCreateFile INVALID_RECORD_FILE_EXISTS = new DataCreateFile(
            1,
            nameTestFile4,
            new ArrayList<>(),
            new ArrayList<>()
    );
    DataCreateFile INVALID_RECORD_TYPE_FILE = new DataCreateFile(
            9999,
            "failFile",
            new ArrayList<>(),
            new ArrayList<>()
    );
    DataCreateFile INVALID_RECORD_PATH_DIRECTORY = new DataCreateFile(
            1,
            "failFile",
            Arrays.asList("fail", "path", "directory"),
            Arrays.asList("fail", "path", "directory")
    );

    @Test
    public void createFileType1_success() throws NotFoundTypeFileException, IOException {
        final String nameFile = nameTestFile1 + "." + TypeFile.getExtension(RECORD_1.typeFile());
        final Path pathTestFile = UtilsActionForFiles.preparePreliminaryFiles(
                nameFile, RECORD_1.parents(), logger, buildDirectoryPath
        );

        service.create(
                RECORD_1.parents(),
                RECORD_1.nameFile(),
                RECORD_1.typeFile()
        );

        Assertions.assertTrue(Files.exists(pathTestFile));

        UtilsActionForFiles.deleteFileAndWriteLog(pathTestFile, logger);
    }

    @Test
    public void createFileType2_success() throws NotFoundTypeFileException, IOException {
        final String nameFile = nameTestFile2 + "." + TypeFile.getExtension(RECORD_2.typeFile());
        final Path pathTestFile = UtilsActionForFiles.preparePreliminaryFiles(
                nameFile, RECORD_2.parents(), logger, buildDirectoryPath
        );

        service.create(
                RECORD_2.parents(),
                RECORD_2.nameFile(),
                RECORD_2.typeFile()
        );

        Assertions.assertTrue(Files.exists(pathTestFile));

        UtilsActionForFiles.deleteFileAndWriteLog(pathTestFile, logger);
    }

    @Test
    public void createFileType3_success() throws NotFoundTypeFileException, IOException {
        final String nameFile = nameTestFile3 + "." + TypeFile.getExtension(RECORD_3.typeFile());
        final Path pathTestFile = UtilsActionForFiles.preparePreliminaryFiles(
                nameFile, RECORD_3.parents(), logger, buildDirectoryPath
        );

        service.create(
                RECORD_3.parents(),
                RECORD_3.nameFile(),
                RECORD_3.typeFile()
        );

        Assertions.assertTrue(Files.exists(pathTestFile));

        UtilsActionForFiles.deleteFileAndWriteLog(pathTestFile, logger);
    }

    @Test
    public void createFile_fail_fileIsExists() throws IOException {
        final String nameFile =
                INVALID_RECORD_FILE_EXISTS.nameFile()
                + "."
                + TypeFile.getExtension(INVALID_RECORD_FILE_EXISTS.typeFile());
        final Path pathTestFile = UtilsActionForFiles.preparePreliminaryFiles(
                nameFile, INVALID_RECORD_FILE_EXISTS.parents(), logger, buildDirectoryPath
        );

        final Path newPathTestFile = Files.createFile(pathTestFile);

        Assertions.assertThrows(
                FileAlreadyExistsException.class,
                () -> service.create(
                        INVALID_RECORD_FILE_EXISTS.parents(),
                        INVALID_RECORD_FILE_EXISTS.nameFile(),
                        INVALID_RECORD_FILE_EXISTS.typeFile()
                )
        );

        Assertions.assertTrue(Files.exists(newPathTestFile));

        UtilsActionForFiles.deleteFileAndWriteLog(newPathTestFile, logger);
    }

    @Test
    public void createFile_fail_invalidTypeFile() {
        Assertions.assertThrows(
                NotFoundTypeFileException.class,
                () -> service.create(
                        INVALID_RECORD_TYPE_FILE.parents(),
                        INVALID_RECORD_TYPE_FILE.nameFile(),
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
                        INVALID_RECORD_PATH_DIRECTORY.nameFile(),
                        INVALID_RECORD_PATH_DIRECTORY.typeFile()
                )
        );
    }
}
