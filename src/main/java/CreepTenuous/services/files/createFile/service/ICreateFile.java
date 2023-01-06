package CreepTenuous.services.files.createFile.service;

import CreepTenuous.services.directory.builderDirectory.enums.Directory;
import CreepTenuous.services.files.enums.ExceptionFile;
import CreepTenuous.services.files.enums.TypeFile;
import CreepTenuous.services.files.createFile.exceptions.NotFoundTypeFileException;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public interface ICreateFile extends ICreateFileExcel, ICreateFileDocx {
    String txt = "txt";
    String document = "docx";
    String excel = "xlsx";

    void create(List<String> parents, String nameFile, Integer typeFile) throws NotFoundTypeFileException, IOException;

    default void checkTypeFile(Integer typeFile) throws NotFoundTypeFileException {
        List<Integer> types = TypeFile.getTypesCode();
        if (!types.contains(typeFile)) {
            throw new NotFoundTypeFileException(ExceptionFile.NOT_FOUND_TYPE_FILE.get());
        }
    }

    default void conductor(Path path, String nameFile, Integer typeFile) throws IOException {
        String typeFileStr = TypeFile.getExtension(typeFile);
        String newNameFile = nameFile + "." + typeFileStr;
        Path newPathFile = Path.of(path.toString() + Directory.SEPARATOR.get() + newNameFile);

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
