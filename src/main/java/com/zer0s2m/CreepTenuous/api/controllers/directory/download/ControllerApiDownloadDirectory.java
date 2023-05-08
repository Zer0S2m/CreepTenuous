package com.zer0s2m.CreepTenuous.api.controllers.directory.download;

import com.zer0s2m.CreepTenuous.api.controllers.directory.download.data.DataDownloadDirectory;
import com.zer0s2m.CreepTenuous.api.core.annotations.V1APIRestController;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;
import com.zer0s2m.CreepTenuous.providers.redis.controllers.CheckRightsActionFileSystem;
import com.zer0s2m.CreepTenuous.services.directory.download.services.impl.ServiceDownloadDirectory;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;
import com.zer0s2m.CreepTenuous.services.directory.download.services.impl.ServiceDownloadDirectoryRedis;
import com.zer0s2m.CreepTenuous.utils.CloneList;
import com.zer0s2m.CreepTenuous.utils.WalkDirectoryInfo;
import com.zer0s2m.CreepTenuous.utils.containers.ContainerInfoFileSystemObject;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@V1APIRestController
public class ControllerApiDownloadDirectory implements CheckIsExistsDirectoryApi, CheckRightsActionFileSystem {
    private final ServiceBuildDirectoryPath buildDirectoryPath;

    private final ServiceDownloadDirectory serviceDownloadDirectory;

    private final ServiceDownloadDirectoryRedis serviceDownloadDirectoryRedis;

    @Autowired
    public ControllerApiDownloadDirectory(
            ServiceBuildDirectoryPath buildDirectoryPath,
            ServiceDownloadDirectory serviceDownloadDirectory,
            ServiceDownloadDirectoryRedis serviceDownloadDirectoryRedis
    ) {
        this.buildDirectoryPath = buildDirectoryPath;
        this.serviceDownloadDirectory = serviceDownloadDirectory;
        this.serviceDownloadDirectoryRedis = serviceDownloadDirectoryRedis;
    }

    @GetMapping(path = "/directory/download")
    public final ResponseEntity<Resource> download(
            final @Valid DataDownloadDirectory data,
            @RequestHeader(name = "Authorization") String accessToken
    ) throws IOException {
        serviceDownloadDirectoryRedis.setAccessToken(accessToken);
        serviceDownloadDirectoryRedis.setEnableCheckIsNameDirectory(true);
        serviceDownloadDirectoryRedis.checkRights(
                CloneList.cloneOneLevel(data.parents()),
                CloneList.cloneOneLevel(data.systemParents()),
                data.systemDirectory()
        );

        List<String> cloneSystemParents = CloneList.cloneOneLevel(data.systemParents());
        cloneSystemParents.add(data.systemDirectory());

        HashMap<String, String> resource = serviceDownloadDirectoryRedis.getResource(
                WalkDirectoryInfo.walkDirectory(Path.of(buildDirectoryPath.build(cloneSystemParents)))
                        .stream()
                        .map(ContainerInfoFileSystemObject::nameFileSystemObject)
                        .collect(Collectors.toList())
        );

        serviceDownloadDirectory.setMap(resource);

        return serviceDownloadDirectory.download(data.systemParents(), data.systemDirectory());
    }
}
