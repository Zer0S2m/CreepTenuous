package com.zer0s2m.CreepTenuous.Helpers;

import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public interface UtilsActionForFiles {
    static void deleteFileAndWriteLog(Path path, Logger logger) throws IOException {
        logger.info("Is deleted file or folder for tests: " + Files.deleteIfExists(path) + " (" + path + ")");
    }
}
