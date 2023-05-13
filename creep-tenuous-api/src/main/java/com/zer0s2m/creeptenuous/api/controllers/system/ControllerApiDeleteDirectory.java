package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.data.DataDeleteDirectoryApi;
import com.zer0s2m.creeptenuous.common.utils.CloneList;
import com.zer0s2m.creeptenuous.services.redis.system.ServiceDeleteDirectoryRedisImpl;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceDeleteDirectoryImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.nio.file.NoSuchFileException;

@V1APIRestController
public class ControllerApiDeleteDirectory {
    private final ServiceDeleteDirectoryImpl deleteDirectory;

    private final ServiceDeleteDirectoryRedisImpl deleteDirectoryRedis;

    @Autowired
    public ControllerApiDeleteDirectory(
            ServiceDeleteDirectoryImpl deleteDirectory,
            ServiceDeleteDirectoryRedisImpl deleteDirectoryRedis
    ) {
        this.deleteDirectory = deleteDirectory;
        this.deleteDirectoryRedis = deleteDirectoryRedis;
    }

    @DeleteMapping("/directory/delete")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public final void deleteDirectory(
            final @Valid @RequestBody DataDeleteDirectoryApi directoryForm,
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
