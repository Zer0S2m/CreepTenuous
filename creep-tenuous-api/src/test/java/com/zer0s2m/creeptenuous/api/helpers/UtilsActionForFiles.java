package com.zer0s2m.creeptenuous.api.helpers;

import com.zer0s2m.creeptenuous.core.services.ServiceBuildDirectoryPath;
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
        Path pathTestFile = Paths.get(build.build(parents), nameFile);
        logger.info("Create resource for tests: " + pathTestFile);
        return pathTestFile;
    }

    static void createDirectories(
            List<String> folders,
            ServiceBuildDirectoryPath build,
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

    static Path preparePreliminaryFilesForCopyDirectories(
            ServiceBuildDirectoryPath build,
            Logger logger,
            List<String> directories,
            String nameFile
    ) throws IOException {
        UtilsActionForFiles.createDirectories(directories, build, logger);

        Path pathTestFile1 = UtilsActionForFiles.preparePreliminaryFiles(
                nameFile,
                directories,
                logger,
                build
        );
        Files.createFile(pathTestFile1);
        return pathTestFile1;
    }

}
