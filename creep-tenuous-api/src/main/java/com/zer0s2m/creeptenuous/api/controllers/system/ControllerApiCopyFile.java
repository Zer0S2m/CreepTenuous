package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataCopyFile;
import com.zer0s2m.creeptenuous.common.data.DataCopyFileApi;
import com.zer0s2m.creeptenuous.common.http.ResponseCopyFileApi;
import com.zer0s2m.creeptenuous.common.utils.CloneList;
import com.zer0s2m.creeptenuous.core.handlers.AtomicSystemCallManager;
import com.zer0s2m.creeptenuous.services.redis.system.ServiceCopyFileRedisImpl;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceCopyFileImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@V1APIRestController
public class ControllerApiCopyFile {
    private final ServiceCopyFileImpl serviceCopyFile;

    private final ServiceCopyFileRedisImpl serviceCopyFileRedis;

    @Autowired
    public ControllerApiCopyFile(
            ServiceCopyFileImpl serviceCopyFile,
            ServiceCopyFileRedisImpl serviceCopyFileRedis
    ) {
        this.serviceCopyFile = serviceCopyFile;
        this.serviceCopyFileRedis = serviceCopyFileRedis;
    }

    @PostMapping("/file/copy")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseCopyFileApi copy(
            final @Valid @RequestBody DataCopyFileApi file,
            @RequestHeader(name = "Authorization") String accessToken
    ) throws IOException, InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        serviceCopyFileRedis.setAccessToken(accessToken);
        List<String> mergeRealAndSystemParents = CloneList.cloneOneLevel(
                file.systemParents(),
                file.systemToParents()
        );
        serviceCopyFileRedis.checkRights(file.parents(), mergeRealAndSystemParents, null);
        if (file.systemNameFile() != null) {
            serviceCopyFileRedis.checkRights(file.systemNameFile());
            ContainerDataCopyFile containerData = AtomicSystemCallManager.call(
                    serviceCopyFile,
                    file.systemNameFile(),
                    file.systemParents(),
                    file.systemToParents()
            );
            serviceCopyFileRedis.copy(containerData.target(), file.systemNameFile(), containerData.systemNameFile());
            return new ResponseCopyFileApi(List.of(containerData));
        } else {
            serviceCopyFileRedis.checkRights(file.systemNameFiles());
            List<ContainerDataCopyFile> containersData = AtomicSystemCallManager.call(
                    serviceCopyFile,
                    Objects.requireNonNull(file.systemNameFiles()),
                    file.systemParents(),
                    file.systemToParents()
            );
            serviceCopyFileRedis.copy(
                    containersData.stream().map(ContainerDataCopyFile::target).collect(Collectors.toList()),
                    Objects.requireNonNull(file.systemNameFiles()),
                    containersData.stream().map(ContainerDataCopyFile::systemNameFile).collect(Collectors.toList())
            );
            return new ResponseCopyFileApi(containersData);
        }
    }
}
