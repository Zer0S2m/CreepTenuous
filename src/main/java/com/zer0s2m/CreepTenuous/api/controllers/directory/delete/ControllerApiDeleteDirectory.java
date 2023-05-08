package com.zer0s2m.CreepTenuous.api.controllers.directory.delete;

import com.zer0s2m.CreepTenuous.api.controllers.directory.delete.data.FormDeleteDirectoryApi;
import com.zer0s2m.CreepTenuous.api.core.annotations.V1APIRestController;
import com.zer0s2m.CreepTenuous.providers.redis.controllers.CheckRightsActionFileSystem;
import com.zer0s2m.CreepTenuous.services.directory.delete.services.impl.ServiceDeleteDirectory;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;
import com.zer0s2m.CreepTenuous.services.directory.delete.services.impl.ServiceDeleteDirectoryRedis;

import com.zer0s2m.CreepTenuous.utils.CloneList;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.nio.file.NoSuchFileException;

@V1APIRestController
public class ControllerApiDeleteDirectory implements CheckIsExistsDirectoryApi, CheckRightsActionFileSystem {
    private final ServiceDeleteDirectory deleteDirectory;

    private final ServiceDeleteDirectoryRedis deleteDirectoryRedis;

    @Autowired
    public ControllerApiDeleteDirectory(
            ServiceDeleteDirectory deleteDirectory,
            ServiceDeleteDirectoryRedis deleteDirectoryRedis
    ) {
        this.deleteDirectory = deleteDirectory;
        this.deleteDirectoryRedis = deleteDirectoryRedis;
    }

    @DeleteMapping("/directory/delete")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public final void deleteDirectory(
            final @Valid @RequestBody FormDeleteDirectoryApi directoryForm,
            @RequestHeader(name = "Authorization") String accessToken
    ) throws NoSuchFileException {
        deleteDirectoryRedis.setAccessToken(accessToken);
        deleteDirectoryRedis.setEnableCheckIsNameDirectory(true);
        deleteDirectoryRedis.checkRights(
                directoryForm.parents(),
                CloneList.cloneOneLevel(directoryForm.systemParents()),
                directoryForm.systemDirectoryName()
        );
        deleteDirectory.delete(directoryForm.systemParents(), directoryForm.systemDirectoryName());
        deleteDirectoryRedis.delete(directoryForm.systemDirectoryName());
    }
}
