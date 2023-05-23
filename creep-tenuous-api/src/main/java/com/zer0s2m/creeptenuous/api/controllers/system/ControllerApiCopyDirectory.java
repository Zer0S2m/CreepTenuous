package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.containers.ContainerInfoFileSystemObject;
import com.zer0s2m.creeptenuous.common.data.DataCopyDirectoryApi;
import com.zer0s2m.creeptenuous.common.http.ResponseCopyDirectoryApi;
import com.zer0s2m.creeptenuous.common.utils.CloneList;
import com.zer0s2m.creeptenuous.core.handlers.AtomicSystemCallManager;
import com.zer0s2m.creeptenuous.services.redis.system.ServiceCopyDirectoryRedisImpl;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceCopyDirectoryImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@V1APIRestController
public class ControllerApiCopyDirectory {
    private final ServiceCopyDirectoryImpl serviceCopyDirectory;

    private final ServiceCopyDirectoryRedisImpl serviceCopyDirectoryRedis;

    @Autowired
    public ControllerApiCopyDirectory(
            ServiceCopyDirectoryImpl serviceCopyDirectory,
            ServiceCopyDirectoryRedisImpl serviceCopyDirectoryRedis
    ) {
        this.serviceCopyDirectory = serviceCopyDirectory;
        this.serviceCopyDirectoryRedis = serviceCopyDirectoryRedis;
    }

    @PostMapping("/directory/copy")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseCopyDirectoryApi copy(
            final @Valid @RequestBody DataCopyDirectoryApi dataDirectory,
            @RequestHeader(name = "Authorization") String accessToken
    ) throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        serviceCopyDirectoryRedis.setAccessToken(accessToken);
        serviceCopyDirectoryRedis.setEnableCheckIsNameDirectory(true);
        List<String> mergeParents = CloneList.cloneOneLevel(
                dataDirectory.systemParents(),
                dataDirectory.systemToParents()
        );
        serviceCopyDirectoryRedis.checkRights(
                dataDirectory.parents(),
                mergeParents,
                dataDirectory.systemNameDirectory()
        );

        List<ContainerInfoFileSystemObject> attached = AtomicSystemCallManager.call(
                serviceCopyDirectory,
                dataDirectory.systemParents(),
                dataDirectory.systemToParents(),
                dataDirectory.systemNameDirectory(),
                dataDirectory.method()
        );
        serviceCopyDirectoryRedis.copy(attached);

        return new ResponseCopyDirectoryApi(attached);
    }
}
