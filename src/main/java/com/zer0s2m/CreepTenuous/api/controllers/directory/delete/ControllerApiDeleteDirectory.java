package com.zer0s2m.CreepTenuous.api.controllers.directory.delete;

import com.zer0s2m.CreepTenuous.api.controllers.directory.delete.data.FormDeleteDirectoryApi;
import com.zer0s2m.CreepTenuous.api.core.version.v1.V1APIController;
import com.zer0s2m.CreepTenuous.services.directory.delete.services.impl.ServiceDeleteDirectory;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.nio.file.NoSuchFileException;

@V1APIController
public class ControllerApiDeleteDirectory implements CheckIsExistsDirectoryApi {
    private final ServiceDeleteDirectory deleteDirectory;

    @Autowired
    public ControllerApiDeleteDirectory(ServiceDeleteDirectory deleteDirectory) {
        this.deleteDirectory = deleteDirectory;
    }

    @DeleteMapping("/directory/delete")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public final void deleteDirectory(
            final @Valid @RequestBody FormDeleteDirectoryApi directoryForm
    ) throws NoSuchFileException {
        deleteDirectory.delete(
                directoryForm.parents(),
                directoryForm.name()
        );
    }
}
