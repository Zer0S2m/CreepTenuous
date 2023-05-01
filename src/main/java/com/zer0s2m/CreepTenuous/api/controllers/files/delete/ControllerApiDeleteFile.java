package com.zer0s2m.CreepTenuous.api.controllers.files.delete;

import com.zer0s2m.CreepTenuous.api.controllers.files.delete.data.DataDeleteFile;
import com.zer0s2m.CreepTenuous.api.core.annotations.V1APIRestController;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;
import com.zer0s2m.CreepTenuous.api.controllers.common.exceptions.NoSuchFileExistsException;
import com.zer0s2m.CreepTenuous.providers.redis.controllers.CheckRightsActionFileSystem;
import com.zer0s2m.CreepTenuous.services.files.delete.services.impl.ServiceDeleteFile;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsFileApi;
import com.zer0s2m.CreepTenuous.services.files.delete.services.impl.ServiceDeleteFileRedis;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.nio.file.Path;

@V1APIRestController
public class ControllerApiDeleteFile implements
        CheckIsExistsDirectoryApi, CheckIsExistsFileApi, CheckRightsActionFileSystem {
    private final ServiceDeleteFile serviceDeleteFile;

    private final ServiceDeleteFileRedis serviceDeleteFileRedis;

    @Autowired
    public ControllerApiDeleteFile(
            ServiceDeleteFile serviceDeleteFile,
            ServiceDeleteFileRedis serviceDeleteFileRedis
    ) {
        this.serviceDeleteFile = serviceDeleteFile;
        this.serviceDeleteFileRedis = serviceDeleteFileRedis;
    }

    @DeleteMapping("/file/delete")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteFile(
            final @Valid @RequestBody DataDeleteFile file,
            @RequestHeader(name = "Authorization") String accessToken
    ) throws IOException, NoSuchFileExistsException {
        serviceDeleteFileRedis.setAccessToken(accessToken);
        serviceDeleteFileRedis.checkRights(file.parents(), file.systemParents(), null);
        serviceDeleteFileRedis.checkRights(List.of(file.systemNameFile()));
        Path source = serviceDeleteFile.delete(file.systemNameFile(), file.systemParents());
        serviceDeleteFileRedis.delete(source, file.systemNameFile());
    }
}
