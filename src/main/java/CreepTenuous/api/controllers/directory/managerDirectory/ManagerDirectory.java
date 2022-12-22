package CreepTenuous.api.controllers.directory.managerDirectory;

import CreepTenuous.api.controllers.directory.managerDirectory.data.DataManagerDirectory;
import CreepTenuous.api.core.version.v1.V1APIController;
import CreepTenuous.services.directory.builderDirectory.services.impl.BuilderDirectory;
import CreepTenuous.services.directory.builderDirectory.exceptions.ExceptionBadLevelDirectory;
import CreepTenuous.services.directory.utils.check.CheckIsExistsDirectoryApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@V1APIController
public class ManagerDirectory implements CheckIsExistsDirectoryApi {
    @Autowired
    private BuilderDirectory builderDirectory;

    @GetMapping("/directory")
    @ResponseStatus(code = HttpStatus.OK)
    public DataManagerDirectory main(
            @RequestParam(value = "level", defaultValue = "0") Integer level,
            @RequestParam(value = "parents", defaultValue = "") List<String> parents
    ) throws IOException {
        return builderDirectory.build(parents, level);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ExceptionBadLevelDirectory handleExceptionBadLevel(HttpMessageNotReadableException error) {
        return new ExceptionBadLevelDirectory(error.getMessage());
    }
}