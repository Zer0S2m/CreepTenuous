package CreepTenuous.api.controllers.files.create;

import CreepTenuous.api.controllers.files.create.data.DataCreateFile;
import CreepTenuous.api.core.version.v1.V1APIController;
import CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;
import CreepTenuous.services.files.create.exceptions.NotFoundTypeFileException;
import CreepTenuous.services.files.create.exceptions.messages.FileAlreadyExistsMsg;
import CreepTenuous.services.files.create.exceptions.messages.NotFoundTypeFileMsg;
import CreepTenuous.services.files.create.services.impl.CreateFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

@V1APIController
public class CreateFileApi implements CheckIsExistsDirectoryApi {
    private final CreateFile serviceCreateFile;

    @Autowired
    public CreateFileApi(CreateFile serviceCreateFile) {
        this.serviceCreateFile = serviceCreateFile;
    }

    @PostMapping("/file/create")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void createFile(
            final @RequestBody DataCreateFile file
    ) throws NotFoundTypeFileException, IOException {
        serviceCreateFile.create(
                file.parents(),
                file.nameFile(),
                file.typeFile()
        );
    }

    @ExceptionHandler(NotFoundTypeFileException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public NotFoundTypeFileMsg handleExceptionNotFoundTypeFile(NotFoundTypeFileException error) {
        return new NotFoundTypeFileMsg(error.getMessage());
    }

    @ExceptionHandler(FileAlreadyExistsException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public FileAlreadyExistsMsg handleExceptionFileExists(FileAlreadyExistsException error) {
        return new FileAlreadyExistsMsg(error.getMessage());
    }
}
