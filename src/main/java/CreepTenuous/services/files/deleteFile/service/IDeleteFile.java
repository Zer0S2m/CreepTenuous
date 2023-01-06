package CreepTenuous.services.files.deleteFile.service;

import CreepTenuous.services.files.deleteFile.exceptions.NoSuchFileExistsException;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;

public interface IDeleteFile {
    void delete(String nameFile, List<String> parents) throws IOException, NoSuchFileExistsException;
}
