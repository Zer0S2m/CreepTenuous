package CreepTenuous.api.controllers.directory.manager;

import CreepTenuous.api.controllers.directory.manager.data.DataManagerDirectory;
import CreepTenuous.api.core.version.v1.V1APIController;
import CreepTenuous.services.directory.manager.exceptions.NotValidLevelDirectoryException;
import CreepTenuous.services.directory.manager.services.impl.BuilderDirectory;
import CreepTenuous.services.directory.manager.exceptions.messages.ExceptionBadLevelDirectoryMsg;
import CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@V1APIController
public class ManagerDirectoryApi implements CheckIsExistsDirectoryApi {
    private final BuilderDirectory builderDirectory;

    @GetMapping("/directory")
    @ResponseStatus(code = HttpStatus.OK)
    public DataManagerDirectory main(
            final @RequestParam(value = "level", defaultValue = "0") Integer level,
            final @RequestParam(value = "parents", defaultValue = "") List<String> parents
    ) throws IOException, NotValidLevelDirectoryException {
        return builderDirectory.build(parents, level);
    }

    @Autowired
    public ManagerDirectoryApi(BuilderDirectory builderDirectory) {
        this.builderDirectory = builderDirectory;
    }

    @ExceptionHandler(NotValidLevelDirectoryException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ExceptionBadLevelDirectoryMsg handleExceptionBadLevel(NotValidLevelDirectoryException error) {
        return new ExceptionBadLevelDirectoryMsg(error.getMessage());
    }
}