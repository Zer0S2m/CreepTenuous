package com.zer0s2m.CreepTenuous.helpers;

import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;
import com.zer0s2m.CreepTenuous.services.directory.manager.enums.Directory;

import io.jsonwebtoken.lang.Arrays;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public interface UtilsActionForFiles {
    static void deleteFileAndWriteLog(Path path, Logger logger) throws IOException {
        logger.info("Is deleted file or folder for tests: " + Files.deleteIfExists(path) + " (" + path + ")");
    }

    static Path preparePreliminaryFiles(
            String nameFile,
            List<String> parents,
            Logger logger,
            ServiceBuildDirectoryPath build
    ) throws NoSuchFileException {
        Path pathTestFile = Paths.get(build.build(parents) + Directory.SEPARATOR.get() + nameFile);
        logger.info("Create resource for tests: " + pathTestFile);
        return pathTestFile;
    }

    static void createDirectories(
            List<String> folders,
            ServiceBuildDirectoryPath build,
            String rootPath,
            Logger logger
    ) throws NoSuchFileException {
        List<String> foldersNew = new ArrayList<>();

        for (String folder: folders) {
            String path = build.build(foldersNew);
            Path pathNewDirectory = Paths.get(path, folder);
            File newDirectory = pathNewDirectory.toFile();
            logger.info("Create new directory: " + newDirectory + " (" + newDirectory.mkdir() + ")");
            foldersNew.add(folder);
        }
    }
}
