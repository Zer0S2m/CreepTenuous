package CreepTenuous.services.files.deleteFile.service;

import CreepTenuous.api.controllers.common.exceptions.NoSuchFileExistsException;

import java.io.IOException;
import java.util.List;

public interface IDeleteFile {
    void delete(String nameFile, List<String> parents) throws IOException, NoSuchFileExistsException;
}
