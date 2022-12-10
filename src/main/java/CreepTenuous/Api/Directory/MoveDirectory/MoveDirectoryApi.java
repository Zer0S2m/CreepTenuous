package CreepTenuous.Api.Directory.MoveDirectory;

import CreepTenuous.Api.Directory.MoveDirectory.forms.FormMoveDirectoryApi;
import CreepTenuous.Api.core.version.v1.V1APIController;
import CreepTenuous.Directory.MoveDirectory.services.impl.MoveDirectory;

import CreepTenuous.Directory.utils.check.CheckIsExistsDirectoryApi;
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
