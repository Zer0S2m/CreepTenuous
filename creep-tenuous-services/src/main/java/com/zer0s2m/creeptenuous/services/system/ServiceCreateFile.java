package com.zer0s2m.creeptenuous.services.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataCreateFile;
import com.zer0s2m.creeptenuous.common.enums.ExceptionFile;
import com.zer0s2m.creeptenuous.common.enums.TypeFile;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundTypeFileException;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public interface ServiceCreateFile extends ServiceCreateFileExcel, ServiceCreateFileDocx {
    String txt = "txt";
    String document = "docx";
    String excel = "xlsx";

    ContainerDataCreateFile create(List<String> parents, String nameFile, Integer typeFile)
            throws NotFoundTypeFileException, IOException;

    default void checkTypeFile(Integer typeFile) throws NotFoundTypeFileException {
        List<Integer> types = TypeFile.getTypesCode();
        if (!types.contains(typeFile)) {
            throw new NotFoundTypeFileException(ExceptionFile.NOT_FOUND_TYPE_FILE.get());
        }
    }

    default void conductor(Path path, String nameFile, Integer typeFile) throws IOException {
        String typeFileStr = TypeFile.getExtension(typeFile);
        String newNameFile = nameFile + "." + typeFileStr;
        Path newPathFile = Path.of(path.toString(), newNameFile);

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