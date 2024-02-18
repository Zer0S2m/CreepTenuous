package com.zer0s2m.creeptenuous.core.services;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataCreateFile;
import com.zer0s2m.creeptenuous.common.enums.ExceptionFile;
import com.zer0s2m.creeptenuous.common.enums.TypeFile;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundTypeFileException;
import com.zer0s2m.creeptenuous.common.utils.UtilsFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.AtomicServiceFileSystem;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * Service for serving the creation of files of different formats.
 * <ul>
 *     <li>txt format</li>
 *     <li>xlsx format</li>
 *     <li>docx format</li>
 * </ul>
 */
public interface ServiceCreateFile extends ServiceCreateFileExcel, ServiceCreateFileDocx, AtomicServiceFileSystem {

    String txt = "txt";

    String document = "docx";

    String excel = "xlsx";

    /**
     * Create file with specific format
     * @param parents system path part directories
     * @param nameFile file name
     * @param typeFile type file {@link TypeFile}
     * @return information about the created file
     * @throws NotFoundTypeFileException file format not found to generate it
     * @throws IOException if an I/O error occurs or the parent directory does not exist
     */
    ContainerDataCreateFile create(List<String> parents, String nameFile, Integer typeFile)
            throws NotFoundTypeFileException, IOException;

    /**
     * Checking for the existence of a file format
     * @param typeFile type file {@link TypeFile}
     * @throws NotFoundTypeFileException file format not found to generate it
     */
    default void checkTypeFile(Integer typeFile) throws NotFoundTypeFileException {
        List<Integer> types = TypeFile.getTypesCode();
        if (!types.contains(typeFile)) {
            throw new NotFoundTypeFileException(ExceptionFile.NOT_FOUND_TYPE_FILE.get());
        }
    }

    /**
     * Conductor for creating a file of a specific format
     * @param path target path
     * @param nameFile file name
     * @param typeFile type file {@link TypeFile}
     * @throws IOException if an I/O error occurs or the parent directory does not exist
     */
    default void conductor(@NotNull Path path, String nameFile, Integer typeFile) throws IOException {
        String typeFileStr = TypeFile.getExtension(typeFile);
        String newNameFile = nameFile + "." + typeFileStr;
        Path newPathFile = Path.of(UtilsFileSystem.clearSystemPathFile(
                Path.of(path.toString(), newNameFile)));

        if (Files.exists(newPathFile)) {
            throw new FileAlreadyExistsException(ExceptionFile.FILE_ALREADY_EXISTS.get());
        }

        if (Objects.equals(typeFileStr, txt)) {
            Files.createFile(newPathFile);
        } else if (Objects.equals(typeFileStr, excel)) {
            createFileExcel(newPathFile);
        } else if (Objects.equals(typeFileStr, document)) {
            createFileDocx(newPathFile);
        }
    }

}
