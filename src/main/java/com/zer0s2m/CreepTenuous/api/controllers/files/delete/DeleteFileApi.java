package com.zer0s2m.CreepTenuous.api.controllers.files.delete;

import com.zer0s2m.CreepTenuous.api.controllers.files.delete.data.DataDeleteFile;
import com.zer0s2m.CreepTenuous.api.core.version.v1.V1APIController;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;
import com.zer0s2m.CreepTenuous.api.controllers.common.exceptions.NoSuchFileExistsException;
import com.zer0s2m.CreepTenuous.services.files.delete.services.impl.DeleteFile;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsFileApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@V1APIController
public class DeleteFileApi implements CheckIsExistsDirectoryApi, CheckIsExistsFileApi {
    private final DeleteFile deleteFile;

    @Autowired
    public DeleteFileApi(DeleteFile deleteFile) {
        this.deleteFile = deleteFile;
    }

    @DeleteMapping("/file/delete")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteFile(
            final @RequestBody DataDeleteFile file
    ) throws IOException, NoSuchFileExistsException {
        deleteFile.delete(
                file.nameFile(),
                file.parents()
        );
    }
}
