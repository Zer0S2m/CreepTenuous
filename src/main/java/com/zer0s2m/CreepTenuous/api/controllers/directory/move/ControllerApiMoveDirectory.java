package com.zer0s2m.CreepTenuous.api.controllers.directory.move;

import com.zer0s2m.CreepTenuous.api.controllers.directory.move.data.FormMoveDirectoryApi;
import com.zer0s2m.CreepTenuous.api.core.annotations.V1APIRestController;
import com.zer0s2m.CreepTenuous.providers.redis.controllers.CheckRightsActionFileSystem;
import com.zer0s2m.CreepTenuous.services.directory.move.containers.ContainerMoveDirectory;
import com.zer0s2m.CreepTenuous.services.directory.move.services.impl.ServiceMoveDirectory;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;
import com.zer0s2m.CreepTenuous.services.directory.move.services.impl.ServiceMoveDirectoryRedis;
import com.zer0s2m.CreepTenuous.utils.CloneList;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@V1APIRestController
public class ControllerApiMoveDirectory implements CheckIsExistsDirectoryApi, CheckRightsActionFileSystem {
    private final ServiceMoveDirectory serviceMoveDirectory;

    private final ServiceMoveDirectoryRedis serviceMoveDirectoryRedis;

    @Autowired
    public ControllerApiMoveDirectory(
            ServiceMoveDirectory serviceMoveDirectory,
            ServiceMoveDirectoryRedis serviceMoveDirectoryRedis
    ) {
        this.serviceMoveDirectory = serviceMoveDirectory;
        this.serviceMoveDirectoryRedis = serviceMoveDirectoryRedis;
    }

    @PostMapping("/directory/move")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public final void move(
            final @Valid @RequestBody FormMoveDirectoryApi dataDirectory,
            @RequestHeader(name = "Authorization") String accessToken
    ) throws IOException {
        List<String> mergeParents = CloneList.cloneOneLevel(
                dataDirectory.systemParents(),
                dataDirectory.systemToParents()
        );
        serviceMoveDirectoryRedis.setAccessToken(accessToken);
        serviceMoveDirectoryRedis.setEnableCheckIsNameDirectory(true);
        serviceMoveDirectoryRedis.checkRights(
                dataDirectory.parents(),
                mergeParents,
                dataDirectory.systemNameDirectory()
        );
        ContainerMoveDirectory infoMoving = serviceMoveDirectory.move(
                dataDirectory.systemParents(),
                dataDirectory.systemToParents(),
                dataDirectory.systemNameDirectory(),
                dataDirectory.method()
        );
        serviceMoveDirectoryRedis.move(infoMoving);
    }
}
