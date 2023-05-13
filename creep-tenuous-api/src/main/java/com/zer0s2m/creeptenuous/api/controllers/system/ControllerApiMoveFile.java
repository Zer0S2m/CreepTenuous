package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.data.DataMoveFileApi;
import com.zer0s2m.creeptenuous.common.utils.CloneList;
import com.zer0s2m.creeptenuous.services.redis.system.ServiceMoveFileRedisImpl;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceMoveFileImpl;
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
public class ControllerApiMoveFile {
    private final ServiceMoveFileImpl serviceMoveFile;

    private final ServiceMoveFileRedisImpl serviceMoveFileRedis;

    @Autowired
    public ControllerApiMoveFile(
            ServiceMoveFileImpl serviceMoveFile,
            ServiceMoveFileRedisImpl serviceMoveFileRedis
    ) {
        this.serviceMoveFile = serviceMoveFile;
        this.serviceMoveFileRedis = serviceMoveFileRedis;
    }

    @PostMapping("/file/move")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void createFile(
            final @Valid @RequestBody DataMoveFileApi file,
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
