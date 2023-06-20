package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiDownloadDirectoryDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.components.RootPath;
import com.zer0s2m.creeptenuous.common.containers.ContainerInfoFileSystemObject;
import com.zer0s2m.creeptenuous.common.data.DataDownloadDirectoryApi;
import com.zer0s2m.creeptenuous.common.data.DataDownloadDirectorySelectApi;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.utils.CloneList;
import com.zer0s2m.creeptenuous.common.utils.UtilsDataApi;
import com.zer0s2m.creeptenuous.core.handlers.AtomicSystemCallManager;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceDownloadDirectoryRedis;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceDownloadDirectorySelectRedis;
import com.zer0s2m.creeptenuous.services.system.ServiceDownloadDirectorySetHeaders;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceDownloadDirectoryImpl;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceDownloadDirectorySelectImpl;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceDownloadDirectorySetHeadersImpl;
import com.zer0s2m.creeptenuous.services.system.utils.WalkDirectoryInfo;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@V1APIRestController
public class ControllerApiDownloadDirectory implements ControllerApiDownloadDirectoryDoc {

    static final OperationRights operationRightsShow = OperationRights.SHOW;

    static final OperationRights operationRights = OperationRights.DOWNLOAD;

    private final ServiceBuildDirectoryPath buildDirectoryPath;

    private final ServiceDownloadDirectoryImpl serviceDownloadDirectory;

    private final ServiceDownloadDirectorySelectImpl serviceDownloadDirectorySelect;

    private final ServiceDownloadDirectorySetHeaders serviceDownloadDirectorySetHeaders =
            new ServiceDownloadDirectorySetHeadersImpl();

    private final ServiceDownloadDirectoryRedis serviceDownloadDirectoryRedis;

    private final ServiceDownloadDirectorySelectRedis serviceDownloadDirectorySelectRedis;

    private final ServiceManagerRights serviceManagerRights;

    private final RootPath rootPath;

    @Autowired
    public ControllerApiDownloadDirectory(
            ServiceBuildDirectoryPath buildDirectoryPath,
            ServiceDownloadDirectoryImpl serviceDownloadDirectory,
            ServiceDownloadDirectorySelectImpl serviceDownloadDirectorySelect,
            ServiceDownloadDirectoryRedis serviceDownloadDirectoryRedis,
            ServiceDownloadDirectorySelectRedis serviceDownloadDirectorySelectRedis,
            ServiceManagerRights serviceManagerRights,
            RootPath rootPath
    ) {
        this.buildDirectoryPath = buildDirectoryPath;
        this.serviceDownloadDirectory = serviceDownloadDirectory;
        this.serviceDownloadDirectorySelect = serviceDownloadDirectorySelect;
        this.serviceDownloadDirectoryRedis = serviceDownloadDirectoryRedis;
        this.serviceDownloadDirectorySelectRedis = serviceDownloadDirectorySelectRedis;
        this.serviceManagerRights = serviceManagerRights;
        this.rootPath = rootPath;
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

        Path sourceZipArchive = AtomicSystemCallManager.call(
                serviceDownloadDirectory,
                data.systemParents(),
                data.systemDirectoryName()
        );
        ByteArrayResource contentBytes = new ByteArrayResource(Files.readAllBytes(sourceZipArchive));
        ResponseEntity<Resource> resourceResponseEntity = ResponseEntity
                .ok()
                .headers(serviceDownloadDirectorySetHeaders.collectHeaders(sourceZipArchive, contentBytes))
                .body(contentBytes);

        Files.delete(sourceZipArchive);

        return resourceResponseEntity;
    }

    /**
     * Download directory with selected file objects
     * <p>Called method via {@link AtomicSystemCallManager} -
     * {@link ServiceDownloadDirectorySelectImpl#download(List)}</p>
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
    @PostMapping(path = "/directory/download/select")
    public ResponseEntity<Resource> downloadSelect(
            final @Valid @RequestBody @NotNull DataDownloadDirectorySelectApi data,
            @RequestHeader(name = "Authorization") String accessToken) throws IOException, InvocationTargetException,
            NoSuchMethodException, InstantiationException, IllegalAccessException {
        serviceManagerRights.setAccessClaims(accessToken);
        serviceManagerRights.setIsWillBeCreated(false);

        serviceDownloadDirectorySelectRedis.setAccessClaims(accessToken);
        serviceDownloadDirectorySelectRedis.setEnableCheckIsNameDirectory(false);
        serviceDownloadDirectorySelectRedis.setIsException(false);

        List<String> uniqueSystemParents = UtilsDataApi.collectUniqueSystemParentsForDownloadDirectorySelect(
                data.attached());
        List<String> uniqueSystemName = UtilsDataApi.collectUniqueSystemNameForDownloadDirectorySelect(
                data.attached());
        boolean isRightsSource = serviceDownloadDirectorySelectRedis.checkRights(
                new ArrayList<>(), uniqueSystemParents, null, false);
        boolean isRightsObjects = serviceDownloadDirectorySelectRedis.checkRights(
                new ArrayList<>(), uniqueSystemName, null, false);
        if (!isRightsSource || !isRightsObjects) {
            if (!isRightsSource) {
                serviceManagerRights.checkRightsByOperation(operationRightsShow, uniqueSystemParents);
            }
            if (!isRightsObjects) {
                serviceManagerRights.checkRightsByOperation(operationRights, uniqueSystemName);
            }
        }

        HashMap<String, String> resource = serviceDownloadDirectoryRedis.getResource(
                WalkDirectoryInfo.walkDirectory(Path.of(rootPath.getRootPath()))
                        .stream()
                        .map(ContainerInfoFileSystemObject::nameFileSystemObject)
                        .collect(Collectors.toList())
        );

        serviceDownloadDirectorySelect.setMap(resource);

        Path sourceZipArchive = AtomicSystemCallManager.call(serviceDownloadDirectorySelect, data.attached());
        ByteArrayResource contentBytes = new ByteArrayResource(Files.readAllBytes(sourceZipArchive));
        ResponseEntity<Resource> resourceResponseEntity = ResponseEntity
                .ok()
                .headers(serviceDownloadDirectorySetHeaders.collectHeaders(sourceZipArchive, contentBytes))
                .body(contentBytes);

        Files.delete(sourceZipArchive);

        return resourceResponseEntity;
    }

}
