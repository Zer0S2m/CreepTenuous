package com.zer0s2m.creeptenuous.services.system;

import com.zer0s2m.creeptenuous.common.components.RootPath;
import com.zer0s2m.creeptenuous.common.data.DataDeleteFileApi;
import com.zer0s2m.creeptenuous.common.enums.Directory;
import com.zer0s2m.creeptenuous.common.exceptions.NoSuchFileExistsException;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceDeleteFileImpl;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagServiceFileSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceFileSystem
public class ServiceDeleteFileTests {
    Logger logger = LogManager.getLogger(ServiceDeleteFileTests.class);

    private final ServiceDeleteFile service = new ServiceDeleteFileImpl();

    private final ServiceBuildDirectoryPath buildDirectoryPath = new ServiceBuildDirectoryPath();

    private final RootPath rootPath = new RootPath();

    private final String testFile1 = "testFile1.txt";

    DataDeleteFileApi RECORD_1 = new DataDeleteFileApi(testFile1, testFile1, new ArrayList<>(), new ArrayList<>());

    DataDeleteFileApi INVALID_RECORD_NOT_IS_EXISTS = new DataDeleteFileApi(
            "notFileIsExists.txt",
            "notFileIsExists.txt",
            new ArrayList<>(),
            new ArrayList<>()
    );

    DataDeleteFileApi INVALID_RECORD_INVALID_PATH_DIRECTORY = new DataDeleteFileApi(
            "testFile.txt",
            "testFile.txt",
            Arrays.asList("invalid", "path", "directory"),
            Arrays.asList("invalid", "path", "directory")
    );

    @Test
    public void deleteFile_success() throws IOException, NoSuchFileExistsException {
        Path pathTestFile = Paths.get(
                buildDirectoryPath.build(RECORD_1.parents()), RECORD_1.fileName()
        );
        Files.createFile(pathTestFile);
        logger.info("Create file for tests: " + pathTestFile);

        Assertions.assertTrue(Files.exists(pathTestFile));

        service.delete(RECORD_1.fileName(), RECORD_1.parents());

        Assertions.assertFalse(Files.exists(pathTestFile));

        logger.info(
                "Is deleted file for tests: " + Files.exists(pathTestFile) + " (" + pathTestFile + ")"
        );
    }

    @Test
    public void deleteFile_fail_fileNotIsExists() throws IOException {
        Path pathTestFile = Paths.get(
                buildDirectoryPath.build(INVALID_RECORD_NOT_IS_EXISTS.systemParents()),
                INVALID_RECORD_NOT_IS_EXISTS.systemFileName()
        );

        Assertions.assertFalse(Files.exists(pathTestFile));
        Assertions.assertThrows(
                NoSuchFileException.class,
                () -> service.delete(
                        INVALID_RECORD_NOT_IS_EXISTS.systemFileName(),
                        INVALID_RECORD_NOT_IS_EXISTS.systemParents()
                )
        );
    }

    @Test
    public void deleteFile_fail_invalidPathDirectory() {
        StringBuilder rawDirectory = new StringBuilder();
        for (String part : INVALID_RECORD_INVALID_PATH_DIRECTORY.parents()) {
            rawDirectory.append(Directory.SEPARATOR.get()).append(part);
        }
        Path pathTestFile = Path.of(rootPath.getRootPath(), rawDirectory.toString());

        Assertions.assertFalse(Files.exists(pathTestFile));
        Assertions.assertThrows(
                NoSuchFileException.class,
                () -> service.delete(
                        INVALID_RECORD_INVALID_PATH_DIRECTORY.systemFileName(),
                        INVALID_RECORD_INVALID_PATH_DIRECTORY.systemParents()
                )
        );
    }

}
