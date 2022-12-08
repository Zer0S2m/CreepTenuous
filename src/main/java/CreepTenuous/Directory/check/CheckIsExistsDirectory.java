package CreepTenuous.Directory.check;

import CreepTenuous.Directory.BuilderDirectory.exceptions.ExceptionNotDirectory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.nio.file.NoSuchFileException;

public interface CheckIsExistsDirectory {
    @ExceptionHandler(NoSuchFileException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    default ExceptionNotDirectory handleExceptionNotDirectory(NoSuchFileException error) {
        return new ExceptionNotDirectory(error.getMessage());
    }
}
