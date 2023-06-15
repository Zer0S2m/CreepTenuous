package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiDownloadDirectoryDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.containers.ContainerInfoFileSystemObject;
import com.zer0s2m.creeptenuous.common.data.DataDownloadDirectoryApi;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.utils.CloneList;
import com.zer0s2m.creeptenuous.core.handlers.AtomicSystemCallManager;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceDownloadDirectoryRedis;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceDownloadDirectoryImpl;
import com.zer0s2m.creeptenuous.services.system.utils.WalkDirectoryInfo;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@V1APIRestController
public class ControllerApiDownloadDirectory implements ControllerApiDownloadDirectoryDoc {

    static final OperationRights operationRightsShow = OperationRights.SHOW;

    private final ServiceBuildDirectoryPath buildDirectoryPath;

    private final ServiceDownloadDirectoryImpl serviceDownloadDirectory;

    private final ServiceDownloadDirectoryRedis serviceDownloadDirectoryRedis;

    private final ServiceManagerRights serviceManagerRights;

    @Autowired
    public ControllerApiDownloadDirectory(
            ServiceBuildDirectoryPath buildDirectoryPath,
            ServiceDownloadDirectoryImpl serviceDownloadDirectory,
            ServiceDownloadDirectoryRedis serviceDownloadDirectoryRedis,
            ServiceManagerRights serviceManagerRights
    ) {
        this.buildDirectoryPath = buildDirectoryPath;
        this.serviceDownloadDirectory = serviceDownloadDirectory;
        this.serviceDownloadDirectoryRedis = serviceDownloadDirectoryRedis;
        this.serviceManagerRights = serviceManagerRights;
    }

    /**
     * Download directory
     * <p>Called method via {@link AtomicSystemCallManager} - {@link ServiceDownloadDirectoryImpl#download(List, String)}</p>
     *
     * @param data        directory download data
     * @param accessToken raw JWT access token
     * @return zip file
     * @throws IOException               if an I/O error occurs or the parent directory does not exist
     * @throws InvocationTargetException Exception thrown by an invoked method or constructor.
     * @throws NoSuchMethodException     Thrown when a particular method cannot be found.
     * @throws InstantiationException    Thrown when an application tries to create an instance of a class
     *                                   using the newInstance method in class {@code Class}.
     * @throws IllegalAccessException    An IllegalAccessException is thrown when an application
     *                                   tries to reflectively create an instance
     */
    @Override
    @PostMapping(path = "/directory/download")
    public final ResponseEntity<Resource> download(final @Valid @RequestBody @NotNull DataDownloadDirectoryApi data,
                                                   @RequestHeader(name = "Authorization") String accessToken)
            throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException,
            IllegalAccessException {
        serviceManagerRights.setAccessClaims(accessToken);
        serviceManagerRights.setIsWillBeCreated(false);

        serviceDownloadDirectoryRedis.setAccessToken(accessToken);
        serviceDownloadDirectoryRedis.setEnableCheckIsNameDirectory(true);
        boolean isRightsSource = serviceDownloadDirectoryRedis.checkRights(
                CloneList.cloneOneLevel(data.parents()),
                CloneList.cloneOneLevel(data.systemParents()),
                data.systemDirectoryName());

        List<String> cloneSystemParents = CloneList.cloneOneLevel(data.systemParents());
        cloneSystemParents.add(data.systemDirectoryName());

        if (!isRightsSource) {
            serviceManagerRights.checkRightsByOperation(operationRightsShow, cloneSystemParents);
            serviceManagerRights.checkRightByOperationDownloadDirectory(data.systemDirectoryName());
        }

        HashMap<String, String> resource = serviceDownloadDirectoryRedis.getResource(
                WalkDirectoryInfo.walkDirectory(Path.of(buildDirectoryPath.build(cloneSystemParents)))
                        .stream()
                        .map(ContainerInfoFileSystemObject::nameFileSystemObject)
                        .collect(Collectors.toList())
        );

        serviceDownloadDirectory.setMap(resource);

        return AtomicSystemCallManager.call(
                serviceDownloadDirectory,
                data.systemParents(),
                data.systemDirectoryName()
        );
    }

}
