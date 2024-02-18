package com.zer0s2m.creeptenuous.core.services;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataBuilderDirectory;
import com.zer0s2m.creeptenuous.common.exceptions.NotValidLevelDirectoryException;
import com.zer0s2m.creeptenuous.core.services.helpers.UtilsActionForFiles;
import com.zer0s2m.creeptenuous.core.services.impl.ServiceManagerDirectoryImpl;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagServiceFileSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceFileSystem
public class ServiceManagerDirectoryTests {

    Logger logger = LogManager.getLogger(ServiceManagerDirectoryTests.class);

    private final ServiceManagerDirectory serviceManagerDirectory = new ServiceManagerDirectoryImpl();

    private final ServiceBuildDirectoryPath serviceBuildDirectoryPath = new ServiceBuildDirectoryPath();

    List<String> DIRECTORIES_1 = List.of("test_folder1");

    @BeforeEach
    void setUp() {
        serviceManagerDirectory.setSystemParents(new ArrayList<>());
    }

    @Test
    public void build_success() throws IOException, NotValidLevelDirectoryException {
        UtilsActionForFiles.createDirectories(DIRECTORIES_1, serviceBuildDirectoryPath, logger);

        Path path1 = Path.of(serviceBuildDirectoryPath.build(DIRECTORIES_1));

        ContainerDataBuilderDirectory data = serviceManagerDirectory.build(new ArrayList<>(), 0);

        Assertions.assertEquals(0, (int) data.levelDirectory());
        Assertions.assertFalse(data.namesSystemFileObject().isEmpty());

        UtilsActionForFiles.deleteFileAndWriteLog(path1, logger);
    }

    @Test
    public void buildInvalidLevel_fail() {
        Assertions.assertThrows(
                NotValidLevelDirectoryException.class,
                () -> serviceManagerDirectory.build(new ArrayList<>(), 10)
        );
    }

}
