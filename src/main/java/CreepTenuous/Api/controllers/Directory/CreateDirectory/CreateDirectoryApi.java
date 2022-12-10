package CreepTenuous.Api.controllers.Directory.CreateDirectory;

import CreepTenuous.Api.controllers.Directory.CreateDirectory.forms.FormCreateDirectoryApi;
import CreepTenuous.Api.core.version.v1.V1APIController;
import CreepTenuous.services.Directory.CreateDirectory.exceptions.ExceptionDirectoryExists;
import CreepTenuous.services.Directory.CreateDirectory.services.impl.CreateDirectory;
import CreepTenuous.services.Directory.utils.check.CheckIsExistsDirectoryApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;

@V1APIController
public class CreateDirectoryApi implements CheckIsExistsDirectoryApi {
    @Autowired
    private CreateDirectory createDirectory;

    @PostMapping("/directory/create")
    @ResponseStatus(code = HttpStatus.CREATED)
    public final void createDirectory(
            @RequestBody FormCreateDirectoryApi directoryForm
    ) throws NoSuchFileException, FileAlreadyExistsException {
        createDirectory.create(
                directoryForm.getParents(),
                directoryForm.getName()
        );
    }

    @ExceptionHandler(FileAlreadyExistsException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ExceptionDirectoryExists handleExceptionDirectoryExists(FileAlreadyExistsException error) {
        return new ExceptionDirectoryExists(error.getMessage());
    }
}
