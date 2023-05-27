package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.data.DataDeleteDirectoryApi;
import com.zer0s2m.creeptenuous.common.utils.CloneList;
import com.zer0s2m.creeptenuous.core.handlers.AtomicSystemCallManager;
import com.zer0s2m.creeptenuous.services.redis.system.ServiceDeleteDirectoryRedisImpl;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceDeleteDirectoryImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@V1APIRestController
public class ControllerApiDeleteDirectory {
    private final ServiceDeleteDirectoryImpl serviceDeleteDirectory;

    private final ServiceDeleteDirectoryRedisImpl serviceDeleteDirectoryRedis;

    @Autowired
    public ControllerApiDeleteDirectory(
            ServiceDeleteDirectoryImpl serviceDeleteDirectory,
            ServiceDeleteDirectoryRedisImpl serviceDeleteDirectoryRedis
    ) {
        this.serviceDeleteDirectory = serviceDeleteDirectory;
        this.serviceDeleteDirectoryRedis = serviceDeleteDirectoryRedis;
    }

    /**
     * Delete directory
     * <p>Called method via {@link AtomicSystemCallManager} - {@link ServiceDeleteDirectoryImpl#delete(List, String)}</p>
     * @param directoryForm directory delete data
     * @param accessToken raw JWT access token
     * @throws InvocationTargetException Exception thrown by an invoked method or constructor.
     * @throws NoSuchMethodException Thrown when a particular method cannot be found.
     * @throws InstantiationException Thrown when an application tries to create an instance of a class
     * using the newInstance method in class {@code Class}.
     * @throws IllegalAccessException An IllegalAccessException is thrown when an application
     * tries to reflectively create an instance
     */
    @DeleteMapping("/directory/delete")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public final void deleteDirectory(
            final @Valid @RequestBody DataDeleteDirectoryApi directoryForm,
            @RequestHeader(name = "Authorization") String accessToken
    ) throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        serviceDeleteDirectoryRedis.setAccessToken(accessToken);
        serviceDeleteDirectoryRedis.setEnableCheckIsNameDirectory(true);
        serviceDeleteDirectoryRedis.checkRights(
                directoryForm.parents(),
                CloneList.cloneOneLevel(directoryForm.systemParents()),
                directoryForm.systemDirectoryName()
        );
        AtomicSystemCallManager.call(
                serviceDeleteDirectory,
                directoryForm.systemParents(),
                directoryForm.systemDirectoryName()
        );
        serviceDeleteDirectoryRedis.delete(directoryForm.systemDirectoryName());
    }
}
