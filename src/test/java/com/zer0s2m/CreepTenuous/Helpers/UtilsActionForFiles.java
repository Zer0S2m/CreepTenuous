package com.zer0s2m.CreepTenuous.Helpers;

import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.BuildDirectoryPath;
import com.zer0s2m.CreepTenuous.services.directory.manager.enums.Directory;

import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public interface UtilsActionForFiles {
    static void deleteFileAndWriteLog(Path path, Logger logger) throws IOException {
        logger.info("Is deleted file or folder for tests: " + Files.deleteIfExists(path) + " (" + path + ")");
    }

    static Path preparePreliminaryFiles(
            String nameFile,
            List<String> parents,
            Logger logger,
            BuildDirectoryPath build
    ) throws NoSuchFileException {
        Path pathTestFile = Paths.get(build.build(parents) + Directory.SEPARATOR.get() + nameFile);
        logger.info("Create resource for tests: " + pathTestFile);
        return pathTestFile;
    }
}
