package com.zer0s2m.CreepTenuous.api.controllers.directory.move;

import com.zer0s2m.CreepTenuous.api.controllers.directory.move.data.FormMoveDirectoryApi;
import com.zer0s2m.CreepTenuous.api.core.version.v1.V1APIController;
import com.zer0s2m.CreepTenuous.services.directory.move.services.impl.ServiceMoveDirectory;

import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@V1APIController
public class ControllerApiMoveDirectory implements CheckIsExistsDirectoryApi {
    private final ServiceMoveDirectory moveDirectory;

    @Autowired
    public ControllerApiMoveDirectory(ServiceMoveDirectory moveDirectory) {
        this.moveDirectory = moveDirectory;
    }

    @PostMapping("/directory/move")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public final void move(
            final @Valid @RequestBody FormMoveDirectoryApi dataDirectory
    ) throws IOException {
        moveDirectory.move(
                dataDirectory.parents(),
                dataDirectory.toParents(),
                dataDirectory.nameDirectory(),
                dataDirectory.method()
        );
    }
}
