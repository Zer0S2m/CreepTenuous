package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.data.DataDeleteFileApi;
import com.zer0s2m.creeptenuous.common.exceptions.NoSuchFileExistsException;
import com.zer0s2m.creeptenuous.services.redis.system.ServiceDeleteFileRedisImpl;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceDeleteFileImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.nio.file.Path;

@V1APIRestController
public class ControllerApiDeleteFile {
    private final ServiceDeleteFileImpl serviceDeleteFile;

    private final ServiceDeleteFileRedisImpl serviceDeleteFileRedis;

    @Autowired
    public ControllerApiDeleteFile(
            ServiceDeleteFileImpl serviceDeleteFile,
            ServiceDeleteFileRedisImpl serviceDeleteFileRedis
    ) {
        this.serviceDeleteFile = serviceDeleteFile;
        this.serviceDeleteFileRedis = serviceDeleteFileRedis;
    }

    @DeleteMapping("/file/delete")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteFile(
            final @Valid @RequestBody DataDeleteFileApi file,
            @RequestHeader(name = "Authorization") String accessToken
    ) throws IOException, NoSuchFileExistsException {
        serviceDeleteFileRedis.setAccessToken(accessToken);
        serviceDeleteFileRedis.checkRights(file.parents(), file.systemParents(), null);
        serviceDeleteFileRedis.checkRights(List.of(file.systemNameFile()));
        Path source = serviceDeleteFile.delete(file.systemNameFile(), file.systemParents());
        serviceDeleteFileRedis.delete(source, file.systemNameFile());
    }
}
