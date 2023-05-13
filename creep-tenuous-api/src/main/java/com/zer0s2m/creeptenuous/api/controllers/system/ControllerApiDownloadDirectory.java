package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.containers.ContainerInfoFileSystemObject;
import com.zer0s2m.creeptenuous.common.data.DataDownloadDirectoryApi;
import com.zer0s2m.creeptenuous.common.utils.CloneList;
import com.zer0s2m.creeptenuous.services.redis.system.ServiceDownloadDirectoryRedisImpl;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceDownloadDirectoryImpl;
import com.zer0s2m.creeptenuous.services.system.utils.WalkDirectoryInfo;
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
public class ControllerApiDownloadDirectory {
    private final ServiceBuildDirectoryPath buildDirectoryPath;

    private final ServiceDownloadDirectoryImpl serviceDownloadDirectory;

    private final ServiceDownloadDirectoryRedisImpl serviceDownloadDirectoryRedis;

    @Autowired
    public ControllerApiDownloadDirectory(
            ServiceBuildDirectoryPath buildDirectoryPath,
            ServiceDownloadDirectoryImpl serviceDownloadDirectory,
            ServiceDownloadDirectoryRedisImpl serviceDownloadDirectoryRedis
    ) {
        this.buildDirectoryPath = buildDirectoryPath;
        this.serviceDownloadDirectory = serviceDownloadDirectory;
        this.serviceDownloadDirectoryRedis = serviceDownloadDirectoryRedis;
    }

    @GetMapping(path = "/directory/download")
    public final ResponseEntity<Resource> download(
            final @Valid DataDownloadDirectoryApi data,
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
