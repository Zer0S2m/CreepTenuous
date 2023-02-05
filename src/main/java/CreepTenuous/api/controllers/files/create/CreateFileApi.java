package CreepTenuous.api.controllers.files.create;

import CreepTenuous.api.controllers.files.create.data.DataCreateFile;
import CreepTenuous.api.core.version.v1.V1APIController;
import CreepTenuous.services.directory.utils.check.CheckIsExistsDirectoryApi;
import CreepTenuous.services.files.create.exceptions.NotFoundTypeFileException;
import CreepTenuous.services.files.create.exceptions.data.FileAlreadyExists;
import CreepTenuous.services.files.create.exceptions.data.NotFoundTypeFile;
import CreepTenuous.services.files.create.service.impl.CreateFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

@V1APIController
public class CreateFileApi implements CheckIsExistsDirectoryApi {
    @Autowired
    private CreateFile serviceCreateFile;

    @PostMapping("/file/create")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void createFile(@RequestBody DataCreateFile file) throws NotFoundTypeFileException, IOException {
        serviceCreateFile.create(
                file.getParents(),
                file.nameFile(),
                file.typeFile()
        );
    }

    @ExceptionHandler(NotFoundTypeFileException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public NotFoundTypeFile handleExceptionNotFoundTypeFile(NotFoundTypeFileException error) {
        return new NotFoundTypeFile(error.getMessage());
    }

    @ExceptionHandler(FileAlreadyExistsException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public FileAlreadyExists handleExceptionFileExists(FileAlreadyExistsException error) {
        return new FileAlreadyExists(error.getMessage());
    }
}
