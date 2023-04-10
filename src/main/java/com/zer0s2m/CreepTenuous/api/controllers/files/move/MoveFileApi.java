package com.zer0s2m.CreepTenuous.api.controllers.files.move;

import com.zer0s2m.CreepTenuous.api.controllers.files.move.data.DataMoveFile;
import com.zer0s2m.CreepTenuous.api.core.version.v1.V1APIController;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsFileApi;
import com.zer0s2m.CreepTenuous.services.files.move.services.impl.MoveFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

@V1APIController
public class MoveFileApi implements CheckIsExistsDirectoryApi, CheckIsExistsFileApi {
    private final MoveFile serviceMoveFile;

    @Autowired
    public MoveFileApi(MoveFile serviceMoveFile) {
        this.serviceMoveFile = serviceMoveFile;
    }

    @PostMapping("/file/move")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void createFile(
            final @RequestBody DataMoveFile file
    ) throws IOException {
        if (file.nameFile() != null) {
            serviceMoveFile.move(
                    file.nameFile(),
                    file.parents(),
                    file.toParents()
            );
        } else {
            serviceMoveFile.move(
                    file.nameFiles(),
                    file.parents(),
                    file.toParents()
            );
        }
    }
}
