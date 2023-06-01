package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiDeleteFileDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.data.DataDeleteFileApi;
import com.zer0s2m.creeptenuous.core.handlers.AtomicSystemCallManager;
import com.zer0s2m.creeptenuous.services.redis.system.ServiceDeleteFileRedisImpl;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceDeleteFileImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.nio.file.Path;

@V1APIRestController
public class ControllerApiDeleteFile implements ControllerApiDeleteFileDoc {
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

    /**
     * Delete file
     * @param file file delete data
     * @param accessToken raw JWT access token
     * @throws InvocationTargetException Exception thrown by an invoked method or constructor.
     * @throws NoSuchMethodException Thrown when a particular method cannot be found.
     * @throws InstantiationException Thrown when an application tries to create an instance of a class
     * using the newInstance method in class {@code Class}.
     * @throws IllegalAccessException An IllegalAccessException is thrown when an application
     * tries to reflectively create an instance
     */
    @Override
    @DeleteMapping("/file/delete")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteFile(
            final @Valid @RequestBody DataDeleteFileApi file,
            @RequestHeader(name = "Authorization") String accessToken
    ) throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        serviceDeleteFileRedis.setAccessToken(accessToken);
        serviceDeleteFileRedis.checkRights(file.parents(), file.systemParents(), null);
        serviceDeleteFileRedis.checkRights(List.of(file.systemFileName()));
        Path source = AtomicSystemCallManager.call(
                serviceDeleteFile,
                file.systemFileName(),
                file.systemParents()
        );
        serviceDeleteFileRedis.delete(source, file.systemFileName());
    }
}
