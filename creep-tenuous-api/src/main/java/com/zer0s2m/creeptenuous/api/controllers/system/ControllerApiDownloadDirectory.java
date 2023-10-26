package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiDownloadDirectoryDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.components.RootPath;
import com.zer0s2m.creeptenuous.common.containers.ContainerInfoFileSystemObject;
import com.zer0s2m.creeptenuous.common.data.DataDownloadDirectoryApi;
import com.zer0s2m.creeptenuous.common.data.DataDownloadDirectorySelectApi;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.exceptions.FileObjectIsFrozenException;
import com.zer0s2m.creeptenuous.common.utils.CloneList;
import com.zer0s2m.creeptenuous.common.utils.UtilsDataApi;
import com.zer0s2m.creeptenuous.core.atomic.handlers.AtomicSystemCallManager;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceDownloadDirectoryRedis;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceDownloadDirectorySelectRedis;
import com.zer0s2m.creeptenuous.services.system.ServiceDownloadDirectorySetHeaders;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceDownloadDirectoryImpl;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceDownloadDirectorySelectImpl;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceDownloadDirectorySetHeadersImpl;
import com.zer0s2m.creeptenuous.common.utils.WalkDirectoryInfo;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@V1APIRestController
public class ControllerApiDownloadDirectory implements ControllerApiDownloadDirectoryDoc {

    static final OperationRights operationRightsShow = OperationRights.SHOW;

    static final OperationRights operationRights = OperationRights.DOWNLOAD;

    private final ServiceBuildDirectoryPath buildDirectoryPath = new ServiceBuildDirectoryPath();

    private final ServiceDownloadDirectoryImpl serviceDownloadDirectory;

    private final ServiceDownloadDirectorySelectImpl serviceDownloadDirectorySelect;

    private final ServiceDownloadDirectorySetHeaders serviceDownloadDirectorySetHeaders =
            new ServiceDownloadDirectorySetHeadersImpl();

    private final ServiceDownloadDirectoryRedis serviceDownloadDirectoryRedis;

    private final ServiceDownloadDirectorySelectRedis serviceDownloadDirectorySelectRedis;

    private final ServiceManagerRights serviceManagerRights;

    private final RootPath rootPath = new RootPath();

    @Autowired
    public ControllerApiDownloadDirectory(
            ServiceDownloadDirectoryImpl serviceDownloadDirectory,
            ServiceDownloadDirectorySelectImpl serviceDownloadDirectorySelect,
            ServiceDownloadDirectoryRedis serviceDownloadDirectoryRedis,
            ServiceDownloadDirectorySelectRedis serviceDownloadDirectorySelectRedis,
            ServiceManagerRights serviceManagerRights) {
        this.serviceDownloadDirectory = serviceDownloadDirectory;
        this.serviceDownloadDirectorySelect = serviceDownloadDirectorySelect;
        this.serviceDownloadDirectoryRedis = serviceDownloadDirectoryRedis;
        this.serviceDownloadDirectorySelectRedis = serviceDownloadDirectorySelectRedis;
        this.serviceManagerRights = serviceManagerRights;
    }

    /**
     * Download directory
     * <p>Called method via {@link AtomicSystemCallManager} - {@link ServiceDownloadDirectoryImpl#download(List, String)}</p>
     *
     * @param data        directory download data
     * @param accessToken raw JWT access token
     * @return zip file
     * @throws IOException                 if an I/O error occurs or the parent directory does not exist
     * @throws InvocationTargetException   Exception thrown by an invoked method or constructor.
     * @throws NoSuchMethodException       Thrown when a particular method cannot be found.
     * @throws InstantiationException      Thrown when an application tries to create an instance of a class
     *                                     using the newInstance method in class {@code Class}.
     * @throws IllegalAccessException      An IllegalAccessException is thrown when an application
     *                                     tries to reflectively create an instance
     * @throws FileObjectIsFrozenException file object is frozen
     */
    @Override
    @PostMapping(path = "/directory/download")
    public final @NotNull ResponseEntity<StreamingResponseBody> download(
            final @Valid @RequestBody @NotNull DataDownloadDirectoryApi data,
            @RequestHeader(name = "Authorization") String accessToken) throws IOException,
            InvocationTargetException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, FileObjectIsFrozenException {
        serviceManagerRights.setAccessClaims(accessToken);
        serviceManagerRights.setIsWillBeCreated(false);
        serviceManagerRights.setIsDirectory(true);

        serviceDownloadDirectoryRedis.setAccessToken(accessToken);
        serviceDownloadDirectoryRedis.setEnableCheckIsNameDirectory(false);
        serviceDownloadDirectoryRedis.setIsException(false);
        boolean isRightsSource = serviceDownloadDirectoryRedis.checkRights(
                data.parents(),
                data.systemParents(),
                null);
        boolean isRightTarget = serviceDownloadDirectoryRedis.checkRights(data.systemDirectoryName());

        List<String> cloneSystemParents = CloneList.cloneOneLevel(data.systemParents());

        if (!isRightsSource || !isRightTarget) {
            if (!isRightsSource) {
                serviceManagerRights.checkRightsByOperation(operationRightsShow, cloneSystemParents);

                boolean isFrozen = serviceDownloadDirectoryRedis.isFrozenFileSystemObject(cloneSystemParents);
                if (isFrozen) {
                    throw new FileObjectIsFrozenException();
                }
            }
            if (!isRightTarget) {
                serviceManagerRights.checkRightByOperationDownloadDirectory(data.systemDirectoryName());

                boolean isFrozen = serviceDownloadDirectoryRedis.isFrozenFileSystemObject(data.systemDirectoryName());
                if (isFrozen) {
                    throw new FileObjectIsFrozenException();
                }
            }
        }

        cloneSystemParents.add(data.systemDirectoryName());

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
        StreamingResponseBody responseBody = serviceDownloadDirectory.getZipFileInStream(sourceZipArchive);

        return ResponseEntity
                .ok()
                .headers(serviceDownloadDirectorySetHeaders.collectHeaders(sourceZipArchive))
                .body(responseBody);
    }

    /**
     * Download directory with selected file objects
     * <p>Called method via {@link AtomicSystemCallManager} -
     * {@link ServiceDownloadDirectorySelectImpl#download(List)}</p>
     *
     * @param data        directory download data
     * @param accessToken raw JWT access token
     * @return zip file
     * @throws IOException                 if an I/O error occurs or the parent directory does not exist
     * @throws InvocationTargetException   Exception thrown by an invoked method or constructor.
     * @throws NoSuchMethodException       Thrown when a particular method cannot be found.
     * @throws InstantiationException      Thrown when an application tries to create an instance of a class
     *                                     using the newInstance method in class {@code Class}.
     * @throws IllegalAccessException      An IllegalAccessException is thrown when an application
     *                                     tries to reflectively create an instance
     * @throws FileObjectIsFrozenException file object is frozen
     */
    @Override
    @PostMapping(path = "/directory/download/select")
    public ResponseEntity<StreamingResponseBody> downloadSelect(
            final @Valid @RequestBody @NotNull DataDownloadDirectorySelectApi data,
            @RequestHeader(name = "Authorization") String accessToken) throws IOException, InvocationTargetException,
            NoSuchMethodException, InstantiationException, IllegalAccessException, FileObjectIsFrozenException {
        serviceManagerRights.setAccessClaims(accessToken);
        serviceManagerRights.setIsWillBeCreated(false);

        serviceDownloadDirectorySelectRedis.setAccessClaims(accessToken);
        serviceDownloadDirectorySelectRedis.setEnableCheckIsNameDirectory(false);
        serviceDownloadDirectorySelectRedis.setIsException(false);

        List<String> uniqueSystemParents = UtilsDataApi.collectUniqueSystemParentsForDownloadDirectorySelect(
                data.attached());
        List<String> uniqueSystemName = UtilsDataApi.collectUniqueSystemNameForDownloadDirectorySelect(
                data.attached());
        boolean isRightsSource = serviceDownloadDirectorySelectRedis.checkRights(uniqueSystemParents);
        boolean isRightsObjects = serviceDownloadDirectorySelectRedis.checkRights(uniqueSystemName);
        if (!isRightsSource || !isRightsObjects) {
            if (!isRightsSource) {
                serviceManagerRights.checkRightsByOperation(operationRightsShow, uniqueSystemParents);

                boolean isFrozen = serviceDownloadDirectorySelectRedis
                        .isFrozenFileSystemObject(uniqueSystemParents);
                if (isFrozen) {
                    throw new FileObjectIsFrozenException();
                }
            }
            if (!isRightsObjects) {
                serviceManagerRights.checkRightsByOperation(operationRights, uniqueSystemName);

                boolean isFrozen = serviceDownloadDirectorySelectRedis
                        .isFrozenFileSystemObject(uniqueSystemName);
                if (isFrozen) {
                    throw new FileObjectIsFrozenException();
                }
            }
        }

        HashMap<String, String> resource = serviceDownloadDirectoryRedis.getResource(
                WalkDirectoryInfo.walkDirectory(Path.of(rootPath.getRootPath()))
                        .stream()
                        .map(ContainerInfoFileSystemObject::nameFileSystemObject)
                        .collect(Collectors.toList())
        );

        serviceDownloadDirectorySelect.setMap(resource);

        Path sourceZipArchive = AtomicSystemCallManager.call(
                serviceDownloadDirectorySelect,
                data.attached()
        );
        StreamingResponseBody responseBody = serviceDownloadDirectorySelect.getZipFileInStream(sourceZipArchive);

        return ResponseEntity
                .ok()
                .headers(serviceDownloadDirectorySetHeaders.collectHeaders(sourceZipArchive))
                .body(responseBody);
    }

}
