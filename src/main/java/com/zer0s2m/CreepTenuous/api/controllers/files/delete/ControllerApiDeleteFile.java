package com.zer0s2m.CreepTenuous.api.controllers.files.delete;

import com.zer0s2m.CreepTenuous.api.controllers.files.delete.data.DataDeleteFile;
import com.zer0s2m.CreepTenuous.api.core.version.v1.V1APIController;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;
import com.zer0s2m.CreepTenuous.api.controllers.common.exceptions.NoSuchFileExistsException;
import com.zer0s2m.CreepTenuous.services.files.delete.services.impl.ServiceDeleteFile;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsFileApi;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@V1APIController
public class ControllerApiDeleteFile implements CheckIsExistsDirectoryApi, CheckIsExistsFileApi {
    private final ServiceDeleteFile deleteFile;

    @Autowired
    public ControllerApiDeleteFile(ServiceDeleteFile deleteFile) {
        this.deleteFile = deleteFile;
    }

    @DeleteMapping("/file/delete")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteFile(
            final @Valid @RequestBody DataDeleteFile file
    ) throws IOException, NoSuchFileExistsException {
        deleteFile.delete(
                file.nameFile(),
                file.parents()
        );
    }
}
