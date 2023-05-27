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

    /**
     * Upload file
     * <p>Called method via {@link AtomicSystemCallManager} - {@link ServiceUploadFileImpl#upload(List, List)}</p>
     * @param files raw files
     * @param parents real names directories
     * @param systemParents parts of the system path - source
     * @param accessToken raw JWT access token
     * @return result upload file
     * @throws InvocationTargetException Exception thrown by an invoked method or constructor.
     * @throws NoSuchMethodException Thrown when a particular method cannot be found.
     * @throws InstantiationException Thrown when an application tries to create an instance of a class
     * using the newInstance method in class {@code Class}.
     * @throws IllegalAccessException An IllegalAccessException is thrown when an application
     * tries to reflectively create an instance
     */
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
