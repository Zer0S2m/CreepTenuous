package com.zer0s2m.CreepTenuous.api.controllers.files.move;

import com.zer0s2m.CreepTenuous.api.controllers.files.move.data.DataMoveFile;
import com.zer0s2m.CreepTenuous.api.core.version.v1.V1APIController;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsFileApi;
import com.zer0s2m.CreepTenuous.services.files.move.services.impl.ServiceMoveFile;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.Objects;

@V1APIController
public class ControllerApiMoveFile implements CheckIsExistsDirectoryApi, CheckIsExistsFileApi {
    private final ServiceMoveFile serviceMoveFile;

    @Autowired
    public ControllerApiMoveFile(ServiceMoveFile serviceMoveFile) {
        this.serviceMoveFile = serviceMoveFile;
    }

    @PostMapping("/file/move")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void createFile(
            final @Valid @RequestBody DataMoveFile file
    ) throws IOException {
        if (file.nameFile() != null) {
            serviceMoveFile.move(
                    file.nameFile(),
                    file.parents(),
                    file.toParents()
            );
        } else {
            serviceMoveFile.move(
                    Objects.requireNonNull(file.nameFiles()),
                    file.parents(),
                    file.toParents()
            );
        }
    }
}
