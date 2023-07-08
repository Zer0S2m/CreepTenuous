package com.zer0s2m.creeptenuous.api.controllers.user;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiControlFileSystemObjectDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.redis.services.user.ServiceControlFileSystemObjectRedis;
import org.springframework.beans.factory.annotation.Autowired;

@V1APIRestController
public class ControllerApiControlFileSystemObject implements ControllerApiControlFileSystemObjectDoc {

    private final ServiceControlFileSystemObjectRedis serviceControlFileSystemObjectRedis;

    @Autowired
    public ControllerApiControlFileSystemObject(
            ServiceControlFileSystemObjectRedis serviceControlFileSystemObjectRedis) {
        this.serviceControlFileSystemObjectRedis = serviceControlFileSystemObjectRedis;
    }

    @Override
    public void freezingFileSystemObject() {
        serviceControlFileSystemObjectRedis.freezingFileSystemObject();
    }

    @Override
    public void unfreezingFileSystemObject() {
        serviceControlFileSystemObjectRedis.unfreezingFileSystemObject();
    }

}
