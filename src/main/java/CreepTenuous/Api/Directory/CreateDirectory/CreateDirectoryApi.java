package CreepTenuous.Api.Directory.CreateDirectory;

import CreepTenuous.Directory.CreateDirectory.exceptions.ExceptionDirectoryExists;
import CreepTenuous.Directory.CreateDirectory.services.impl.CreateDirectory;
import CreepTenuous.Api.Directory.CreateDirectory.forms.FormCreateDirectoryApi;
import CreepTenuous.Directory.utils.check.CheckIsExistsDirectoryApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;

@RestController
@RequestMapping("/directory")
public class CreateDirectoryApi implements CheckIsExistsDirectoryApi {
    @Autowired
    private CreateDirectory createDirectory;

    @PostMapping("/create")
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
