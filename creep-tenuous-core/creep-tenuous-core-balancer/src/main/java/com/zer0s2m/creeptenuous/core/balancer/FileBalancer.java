package com.zer0s2m.creeptenuous.core.balancer;

import com.zer0s2m.creeptenuous.core.balancer.exceptions.FileIsDirectoryException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The main interface for implementing the class responsible for splitting and restoring files
 */
public interface FileBalancer {

    /**
     * Unit of measurement - how many parts the file will be split into
     */
    int PART_COUNTER = 5;

    /**
     * Split file output format
     */
    String FORMAT_OUTPUT_FILE = "%s.%03d";

    /**
     * Divide the source file into several component parts. default quantity - {@link FileBalancer#PART_COUNTER}
     * @param source the path to the file that will be split in the process
     * @return file component paths
     * @throws IOException signals that an I/O exception to some sort has occurred
     * @throws FileIsDirectoryException the exception indicates that the source file object is a directory
     */
    static @NotNull Collection<Path> split(Path source) throws IOException, FileIsDirectoryException {
        return split(source, PART_COUNTER);
    }

    /**
     * Divide the source file into several component parts. default quantity - {@link FileBalancer#PART_COUNTER}
     * @param source the path to the file that will be split in the process
     * @param partCounter unit of measurement - how many parts the file will be split into
     * @return file component paths
     * @throws IOException signals that an I/O exception to some sort has occurred
     * @throws FileIsDirectoryException the exception indicates that the source file object is a directory
     */
    static @NotNull Collection<Path> split(Path source, int partCounter) throws IOException, FileIsDirectoryException {
        if (Files.isDirectory(source)) {
            throw new FileIsDirectoryException();
        }

        Collection<Path> paths = new ArrayList<>();

        int partCount = 1;

        File file = source.toFile();
        String fileName = file.getName();

        byte[] buffer = new byte[(int) (file.length() / partCounter)];

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            int bytesAmount;
            while ((bytesAmount = bufferedInputStream.read(buffer)) > 0) {
                String filePartName = String.format(FORMAT_OUTPUT_FILE, fileName, partCount++);
                File newFile = new File(file.getParent(), filePartName);

                try (FileOutputStream outputStream = new FileOutputStream(newFile)) {
                    outputStream.write(buffer, 0, bytesAmount);
                }

                paths.add(newFile.toPath());
            }
        }

        return paths;
    }

    /**
     * Restore a file from its constituent parts to the specified path
     * @param sourceFiles the original parts of the file for its restoration into a single
     * @param into path to the file that will be assembled from parts
     * @return assembled file
     * @throws IOException signals that an I/O exception to some sort has occurred
     * @throws FileIsDirectoryException the exception indicates that the source file object is a directory
     */
    @Contract("_, _ -> param2")
    static Path merge(@NotNull Collection<Path> sourceFiles, Path into) throws IOException, FileIsDirectoryException {
        for (Path sourceFile : sourceFiles) {
            if (Files.isDirectory(sourceFile)) {
                throw new FileIsDirectoryException();
            }
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(into.toFile());
             BufferedOutputStream mergingStream = new BufferedOutputStream(fileOutputStream)) {
            for (Path sourceFile : sourceFiles) {
                Files.copy(sourceFile, mergingStream);
            }
        }

        return into;
    }

}