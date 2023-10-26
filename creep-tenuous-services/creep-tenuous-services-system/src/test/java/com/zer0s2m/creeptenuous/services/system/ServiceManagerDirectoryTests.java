package com.zer0s2m.creeptenuous.services.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataBuilderDirectory;
import com.zer0s2m.creeptenuous.common.exceptions.NotValidLevelDirectoryException;
import com.zer0s2m.creeptenuous.services.system.helpers.UtilsActionForFiles;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceBuilderDataFileSystemObjectImpl;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceCollectDirectoryImpl;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceManagerDirectoryImpl;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagServiceFileSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = {
        ServiceCollectDirectoryImpl.class,
        ServiceBuilderDataFileSystemObjectImpl.class,
        ServiceManagerDirectoryImpl.class,
})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceFileSystem
public class ServiceManagerDirectoryTests {

    Logger logger = LogManager.getLogger(ServiceManagerDirectoryTests.class);

    @Autowired
    private ServiceManagerDirectoryImpl serviceManagerDirectory;

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
