package com.zer0s2m.CreepTenuous.api.controllers.files.copy;

import com.zer0s2m.CreepTenuous.api.controllers.files.copy.data.DataCopyFile;
import com.zer0s2m.CreepTenuous.api.core.version.v1.V1APIController;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsFileApi;
import com.zer0s2m.CreepTenuous.services.files.copy.services.impl.CopyFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

@V1APIController
public class CopyFileApi implements CheckIsExistsDirectoryApi, CheckIsExistsFileApi {
    private final CopyFile serviceCopyFile;

    @Autowired
    public CopyFileApi(CopyFile serviceCopyFile) {
        this.serviceCopyFile = serviceCopyFile;
    }

    @PostMapping("/file/copy")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void createFile(
            final @RequestBody DataCopyFile file
    ) throws IOException {
        if (file.nameFile() != null) {
            serviceCopyFile.copy(
                    file.nameFile(),
                    file.parents(),
                    file.toParents()
            );
        } else {
            serviceCopyFile.copy(
                    file.nameFiles(),
                    file.parents(),
                    file.toParents()
            );
        }
    }
}
