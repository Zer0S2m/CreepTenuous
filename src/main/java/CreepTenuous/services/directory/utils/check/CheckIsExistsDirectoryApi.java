package CreepTenuous.services.directory.utils.check;

import CreepTenuous.services.directory.builder.exceptions.ExceptionNotDirectory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.nio.file.NoSuchFileException;

public interface CheckIsExistsDirectoryApi {
    @ExceptionHandler(NoSuchFileException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    default ExceptionNotDirectory handleExceptionNotDirectory(NoSuchFileException error) {
        return new ExceptionNotDirectory(error.getMessage());
    }
}
