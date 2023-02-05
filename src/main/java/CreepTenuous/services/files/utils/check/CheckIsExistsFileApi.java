package CreepTenuous.services.files.utils.check;

import CreepTenuous.api.controllers.common.exceptions.NoSuchFileExistsException;
import CreepTenuous.api.controllers.common.exceptions.data.NoSuchFileExists;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

public interface CheckIsExistsFileApi {
    @ExceptionHandler(NoSuchFileExistsException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    default NoSuchFileExists handleExceptionFileNotExists(NoSuchFileExistsException error) {
        return new NoSuchFileExists(error.getMessage());
    }
}
