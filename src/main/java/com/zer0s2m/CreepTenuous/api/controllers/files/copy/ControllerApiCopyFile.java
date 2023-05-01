package com.zer0s2m.CreepTenuous.api.controllers.files.copy;

import com.zer0s2m.CreepTenuous.api.controllers.files.copy.data.DataCopyFile;
import com.zer0s2m.CreepTenuous.api.core.annotations.V1APIRestController;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsFileApi;
import com.zer0s2m.CreepTenuous.providers.redis.controllers.CheckRightsActionFileSystem;
import com.zer0s2m.CreepTenuous.services.files.copy.services.impl.ServiceCopyFile;
import com.zer0s2m.CreepTenuous.services.files.copy.services.impl.ServiceCopyFileRedis;
import com.zer0s2m.CreepTenuous.services.files.move.containers.ContainerMovingFiles;
import com.zer0s2m.CreepTenuous.utils.CloneList;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@V1APIRestController
public class ControllerApiCopyFile implements
        CheckIsExistsDirectoryApi, CheckIsExistsFileApi, CheckRightsActionFileSystem {
    private final ServiceCopyFile serviceCopyFile;

    private final ServiceCopyFileRedis serviceCopyFileRedis;

    @Autowired
    public ControllerApiCopyFile(ServiceCopyFile serviceCopyFile, ServiceCopyFileRedis serviceCopyFileRedis) {
        this.serviceCopyFile = serviceCopyFile;
        this.serviceCopyFileRedis = serviceCopyFileRedis;
    }

    @PostMapping("/file/copy")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void createFile(
            final @Valid @RequestBody DataCopyFile file,
            @RequestHeader(name = "Authorization") String accessToken
    ) throws IOException {
        serviceCopyFileRedis.setAccessToken(accessToken);
        List<String> mergeRealAndSystemParents = CloneList.cloneOneLevel(
                file.systemParents(),
                file.systemToParents()
        );
        serviceCopyFileRedis.checkRights(file.parents(), mergeRealAndSystemParents, null);
        if (file.systemNameFile() != null) {
            serviceCopyFileRedis.checkRights(file.systemNameFile());
            ContainerMovingFiles containerData = serviceCopyFile.copy(
                    file.systemNameFile(),
                    file.systemParents(),
                    file.systemToParents()
            );
            serviceCopyFileRedis.copy(containerData.target(), file.systemNameFile(), containerData.systemNameFile());
        } else {
            serviceCopyFileRedis.checkRights(file.systemNameFiles());
            List<ContainerMovingFiles> containersData = serviceCopyFile.copy(
                    Objects.requireNonNull(file.systemNameFiles()),
                    file.systemParents(),
                    file.systemToParents()
            );
            serviceCopyFileRedis.copy(
                    containersData.stream().map(ContainerMovingFiles::target).collect(Collectors.toList()),
                    Objects.requireNonNull(file.systemNameFiles()),
                    containersData.stream().map(ContainerMovingFiles::systemNameFile).collect(Collectors.toList())
            );
        }
    }
}
