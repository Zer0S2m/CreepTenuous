package com.zer0s2m.CreepTenuous.api.controllers.directory.move;

import com.zer0s2m.CreepTenuous.api.controllers.directory.move.data.FormMoveDirectoryApi;
import com.zer0s2m.CreepTenuous.api.core.version.v1.V1APIController;
import com.zer0s2m.CreepTenuous.services.directory.move.services.impl.MoveDirectory;

import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@V1APIController
public class MoveDirectoryApi implements CheckIsExistsDirectoryApi {
    private final MoveDirectory moveDirectory;

    @Autowired
    public MoveDirectoryApi(MoveDirectory moveDirectory) {
        this.moveDirectory = moveDirectory;
    }

    @PostMapping("/directory/move")
    @ResponseStatus(code = HttpStatus.OK)
    public final void move(
            final @RequestBody FormMoveDirectoryApi dataDirectory
    ) throws IOException {
        moveDirectory.move(
                dataDirectory.parents(),
                dataDirectory.toParents(),
                dataDirectory.nameDirectory()
        );
    }
}
