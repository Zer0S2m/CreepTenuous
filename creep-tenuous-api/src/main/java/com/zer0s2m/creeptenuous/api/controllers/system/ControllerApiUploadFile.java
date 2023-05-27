package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataUploadFile;
import com.zer0s2m.creeptenuous.common.http.ResponseObjectUploadFileApi;
import com.zer0s2m.creeptenuous.common.http.ResponseUploadFileApi;
import com.zer0s2m.creeptenuous.core.handlers.AtomicSystemCallManager;
import com.zer0s2m.creeptenuous.services.redis.system.ServiceUploadFileRedisImpl;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceUploadFileImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

@V1APIRestController
public class ControllerApiUploadFile {
    private final ServiceUploadFileImpl serviceUploadFile;

    private final ServiceUploadFileRedisImpl serviceUploadFileRedis;

    @Autowired
    public ControllerApiUploadFile(
            ServiceUploadFileImpl serviceUploadFile,
            ServiceUploadFileRedisImpl serviceUploadFileRedis
    ) {
        this.serviceUploadFile = serviceUploadFile;
        this.serviceUploadFileRedis = serviceUploadFileRedis;
    }

    @PostMapping(value = "/file/upload")
    public ResponseUploadFileApi upload(
            final @RequestPart("files") List<MultipartFile> files,
            final @RequestParam("parents") List<String> parents,
            final @RequestParam("systemParents") List<String> systemParents,
            @RequestHeader(name = "Authorization") String accessToken
    ) throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        serviceUploadFileRedis.setAccessToken(accessToken);
        serviceUploadFileRedis.checkRights(parents, systemParents, null);

        List<ResponseObjectUploadFileApi> data = AtomicSystemCallManager.call(
                serviceUploadFile,
                files,
                systemParents
        );
        serviceUploadFileRedis.create(data
                .stream()
                .map((obj) -> {
                    if (obj.success()) {
                        return new ContainerDataUploadFile(
                                obj.realFileName(),
                                obj.systemFileName(),
                                obj.realPath(),
                                obj.systemPath()
                        );
                    }
                    return null;
                })
                .collect(Collectors.toList()));
        return new ResponseUploadFileApi(data);
    }
}
