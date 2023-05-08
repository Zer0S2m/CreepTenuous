package com.zer0s2m.CreepTenuous.api.controllers.files.move;

import com.zer0s2m.CreepTenuous.api.controllers.files.move.data.DataMoveFile;
import com.zer0s2m.CreepTenuous.api.core.annotations.V1APIRestController;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsFileApi;
import com.zer0s2m.CreepTenuous.providers.redis.controllers.CheckRightsActionFileSystem;
import com.zer0s2m.CreepTenuous.services.files.move.services.impl.ServiceMoveFile;

import com.zer0s2m.CreepTenuous.services.files.move.services.impl.ServiceMoveFileRedis;
import com.zer0s2m.CreepTenuous.utils.CloneList;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.nio.file.Path;
import java.io.IOException;
import java.util.Objects;
import java.util.List;

@V1APIRestController
public class ControllerApiMoveFile implements
        CheckIsExistsDirectoryApi, CheckIsExistsFileApi, CheckRightsActionFileSystem {
    private final ServiceMoveFile serviceMoveFile;

    private final ServiceMoveFileRedis serviceMoveFileRedis;

    @Autowired
    public ControllerApiMoveFile(ServiceMoveFile serviceMoveFile, ServiceMoveFileRedis serviceMoveFileRedis) {
        this.serviceMoveFile = serviceMoveFile;
        this.serviceMoveFileRedis = serviceMoveFileRedis;
    }

    @PostMapping("/file/move")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void createFile(
            final @Valid @RequestBody DataMoveFile file,
            @RequestHeader(name = "Authorization") String accessToken
    ) throws IOException {
        serviceMoveFileRedis.setAccessToken(accessToken);
        List<String> mergeRealAndSystemParents = CloneList.cloneOneLevel(
                file.systemParents(),
                file.systemToParents()
        );
        serviceMoveFileRedis.checkRights(file.parents(), mergeRealAndSystemParents, null);

        if (file.systemNameFile() != null) {
            serviceMoveFileRedis.checkRights(List.of(file.systemNameFile()));
            Path newPathFile = serviceMoveFile.move(
                    file.systemNameFile(),
                    file.systemParents(),
                    file.systemToParents()
            );
            serviceMoveFileRedis.move(newPathFile, file.systemNameFile());
        } else {
            serviceMoveFileRedis.checkRights(file.systemNameFiles());
            Path newPathsFile = serviceMoveFile.move(
                    Objects.requireNonNull(file.systemNameFiles()),
                    file.systemParents(),
                    file.systemToParents()
            );
            serviceMoveFileRedis.move(newPathsFile, file.systemNameFiles());
        }
    }
}
