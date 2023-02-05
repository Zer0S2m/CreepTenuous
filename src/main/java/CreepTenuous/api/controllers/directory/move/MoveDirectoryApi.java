package CreepTenuous.api.controllers.directory.move;

import CreepTenuous.api.controllers.directory.move.forms.FormMoveDirectoryApi;
import CreepTenuous.api.core.version.v1.V1APIController;
import CreepTenuous.services.directory.move.services.impl.MoveDirectory;

import CreepTenuous.services.directory.utils.check.CheckIsExistsDirectoryApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@V1APIController
public class MoveDirectoryApi implements CheckIsExistsDirectoryApi {
    @Autowired
    private MoveDirectory moveDirectory;

    @PostMapping("/directory/move")
    @ResponseStatus(code = HttpStatus.OK)
    public final void move(
            @RequestBody FormMoveDirectoryApi dataDirectory
    ) throws IOException {
        moveDirectory.move(
                dataDirectory.getParents(),
                dataDirectory.getToParents(),
                dataDirectory.getNameDirectory()
        );
    }
}
