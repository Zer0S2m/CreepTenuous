package CreepTenuous.api.controllers.directory.create;

import CreepTenuous.api.controllers.directory.create.data.FormCreateDirectoryApi;
import CreepTenuous.api.core.version.v1.V1APIController;
import CreepTenuous.services.directory.create.exceptions.messages.ExceptionDirectoryExistsMsg;
import CreepTenuous.services.directory.create.services.impl.CreateDirectory;
import CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;

@V1APIController
public class CreateDirectoryApi implements CheckIsExistsDirectoryApi {
    private final CreateDirectory createDirectory;

    @PostMapping("/directory/create")
    @ResponseStatus(code = HttpStatus.CREATED)
    public final void createDirectory(
            final @RequestBody FormCreateDirectoryApi directoryForm
    ) throws NoSuchFileException, FileAlreadyExistsException {
        createDirectory.create(
                directoryForm.parents(),
                directoryForm.name()
        );
    }

    @Autowired
    public CreateDirectoryApi(CreateDirectory createDirectory) {
        this.createDirectory = createDirectory;
    }

    @ExceptionHandler(FileAlreadyExistsException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ExceptionDirectoryExistsMsg handleExceptionDirectoryExists(FileAlreadyExistsException error) {
        return new ExceptionDirectoryExistsMsg(error.getMessage());
    }
}
