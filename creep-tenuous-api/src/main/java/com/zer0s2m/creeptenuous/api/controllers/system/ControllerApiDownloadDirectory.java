package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiDownloadDirectoryDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.components.RootPath;
import com.zer0s2m.creeptenuous.common.containers.ContainerInfoFileSystemObject;
import com.zer0s2m.creeptenuous.common.data.DataDownloadDirectoryApi;
import com.zer0s2m.creeptenuous.common.data.DataDownloadDirectorySelectApi;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.exceptions.FileObjectIsFrozenException;
import com.zer0s2m.creeptenuous.common.exceptions.NoRightsRedisException;
import com.zer0s2m.creeptenuous.common.utils.CloneList;
import com.zer0s2m.creeptenuous.common.utils.UtilsDataApi;
import com.zer0s2m.creeptenuous.core.atomic.AtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.atomic.CoreServiceFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.AtomicSystemCallManager;
import com.zer0s2m.creeptenuous.core.atomic.ServiceFileSystemExceptionHandlerOperationDownload;
import com.zer0s2m.creeptenuous.core.atomic.AtomicServiceFileSystem;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceDownloadDirectoryRedis;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceDownloadDirectorySelectRedis;
import com.zer0s2m.creeptenuous.services.system.ServiceDownloadDirectory;
import com.zer0s2m.creeptenuous.services.system.ServiceDownloadDirectorySelect;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceDownloadDirectoryImpl;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceDownloadDirectorySelectImpl;
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

    private final ServiceDownloadDirectory serviceDownloadDirectory = new ServiceDownloadDirectoryImpl();

    private final ServiceDownloadDirectorySelect serviceDownloadDirectorySelect =
            new ServiceDownloadDirectorySelectImpl();

    private final ServiceDownloadDirectoryRedis serviceDownloadDirectoryRedis;

    private final ServiceDownloadDirectorySelectRedis serviceDownloadDirectorySelectRedis;

    private final ServiceManagerRights serviceManagerRights;

    private final RootPath rootPath = new RootPath();

    private final AtomicFileSystemControllerApiDownloadDirectory atomicFileSystemControllerApiDownloadDirectory =
            new AtomicFileSystemControllerApiDownloadDirectory();

    /**
     * TODO: A temporary crutch - for downloading file objects to which rights are assigned
     */
    private final boolean forceDownloadingFileObjects = true;

    @Autowired
    public ControllerApiDownloadDirectory(
            ServiceDownloadDirectoryRedis serviceDownloadDirectoryRedis,
            ServiceDownloadDirectorySelectRedis serviceDownloadDirectorySelectRedis,
            ServiceManagerRights serviceManagerRights) {
        this.serviceDownloadDirectoryRedis = serviceDownloadDirectoryRedis;
        this.serviceDownloadDirectorySelectRedis = serviceDownloadDirectorySelectRedis;
        this.serviceManagerRights = serviceManagerRights;
    }

    /**
     * Downloading a directory. Supports atomic file system mode.
     * <p>Called via {@link AtomicSystemCallManager} - {@link AtomicFileSystemControllerApiDownloadDirectory#download(DataDownloadDirectoryApi, String)}</p>
     *
     * @param data        Вirectory download data.
     * @param accessToken Кaw JWT access token.
     * @return Zip file.
     * @throws IOException                 Signals that an I/O exception to some sort has occurred.
     * @throws InvocationTargetException   Exception thrown by an invoked method or constructor.
     * @throws NoSuchMethodException       Thrown when a particular method cannot be found.
     * @throws InstantiationException      Thrown when an application tries to create an instance of a class
     *                                     using the newInstance method in class {@code Class}.
     * @throws IllegalAccessException      An IllegalAccessException is thrown when an application
     *                                     tries to reflectively create an instance.
     * @throws FileObjectIsFrozenException File object is frozen.
     */
    @Override
    @PostMapping(path = "/directory/download")
    public final @NotNull ResponseEntity<StreamingResponseBody> download(
            final @Valid @RequestBody @NotNull DataDownloadDirectoryApi data,
            @RequestHeader(name = "Authorization") String accessToken) throws IOException,
            InvocationTargetException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, FileObjectIsFrozenException {
        return AtomicSystemCallManager.call(
                atomicFileSystemControllerApiDownloadDirectory,
                data,
                accessToken
        );
    }

    /**
     * Download directory with selected file objects. Supports atomic file system mode.
     * <p>Called via {@link AtomicSystemCallManager} - {@link AtomicFileSystemControllerApiDownloadDirectory#download(DataDownloadDirectorySelectApi, String)}</p>
     *
     * @param data        Directory download data.
     * @param accessToken Raw JWT access token.
     * @return Zip file.
     * @throws IOException                 Signals that an I/O exception to some sort has occurred.
     * @throws InvocationTargetException   Exception thrown by an invoked method or constructor.
     * @throws NoSuchMethodException       Thrown when a particular method cannot be found.
     * @throws InstantiationException      Thrown when an application tries to create an instance of a class
     *                                     using the newInstance method in class {@code Class}.
     * @throws IllegalAccessException      An IllegalAccessException is thrown when an application
     *                                     tries to reflectively create an instance.
     * @throws FileObjectIsFrozenException File object is frozen.
     */
    @Override
    @PostMapping(path = "/directory/download/select")
    public ResponseEntity<StreamingResponseBody> downloadSelect(
            final @Valid @RequestBody @NotNull DataDownloadDirectorySelectApi data,
            @RequestHeader(name = "Authorization") String accessToken) throws IOException, InvocationTargetException,
            NoSuchMethodException, InstantiationException, IllegalAccessException, FileObjectIsFrozenException {
        return AtomicSystemCallManager.call(
                atomicFileSystemControllerApiDownloadDirectory,
                data,
                accessToken
        );
    }

    @CoreServiceFileSystem(method = "download")
    public final class AtomicFileSystemControllerApiDownloadDirectory implements AtomicServiceFileSystem {

        /**
         * Downloading a directory.
         *
         * @param data        Вirectory download data.
         * @param accessToken Кaw JWT access token.
         * @return Zip file.
         * @throws FileObjectIsFrozenException File object is frozen.
         * @throws IOException                 Signals that an I/O exception to some sort has occurred.
         */
        @SuppressWarnings("unused")
        @AtomicFileSystem(
                name = "download-directory",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                isExceptionMulti = true,
                                handler = ServiceFileSystemExceptionHandlerOperationDownload.class,
                                operation = ContextAtomicFileSystem.Operations.DOWNLOAD
                        )
                }
        )
        public @NotNull ResponseEntity<StreamingResponseBody> download(
                final @NotNull DataDownloadDirectoryApi data, String accessToken)
                throws FileObjectIsFrozenException, IOException {
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
            HashMap<String, String> resource = serviceDownloadDirectoryRedis.getResource(
                    WalkDirectoryInfo.walkDirectory(Path.of(buildDirectoryPath.build(cloneSystemParents)))
                            .stream()
                            .map(ContainerInfoFileSystemObject::nameFileSystemObject)
                            .collect(Collectors.toList())
            );


            if (!isRightsSource || !isRightTarget) {
                if (!isRightsSource) {
                    serviceManagerRights.checkRightsByOperation(operationRightsShow, cloneSystemParents);

                    boolean isFrozen = serviceDownloadDirectoryRedis.isFrozenFileSystemObject(cloneSystemParents);
                    if (isFrozen) {
                        throw new FileObjectIsFrozenException();
                    }
                }
                if (!isRightTarget) {
                    try {
                        serviceManagerRights.checkRightByOperationDownloadDirectory(data.systemDirectoryName());

                        boolean isFrozen = serviceDownloadDirectoryRedis.isFrozenFileSystemObject(data.systemDirectoryName());
                        if (isFrozen) {
                            throw new FileObjectIsFrozenException();
                        }
                    } catch (NoRightsRedisException exception) {
                        if (forceDownloadingFileObjects) {
                            List<ContainerInfoFileSystemObject> allowedFileObjectsDownloading = serviceManagerRights
                                    .getAvailableFileObjectsForDownloading(data.systemDirectoryName());

                            serviceDownloadDirectorySelect.setMap(resource);
                            Path sourceZipArchive = serviceDownloadDirectorySelect
                                    .downloadFromContainers(allowedFileObjectsDownloading);

                            StreamingResponseBody responseBody = UtilsDataApi
                                    .getStreamingResponseBodyFromPath(sourceZipArchive);

                            return ResponseEntity
                                    .ok()
                                    .headers(UtilsDataApi.collectHeadersForZip(sourceZipArchive))
                                    .body(responseBody);
                        }
                    }
                }
            }

            cloneSystemParents.add(data.systemDirectoryName());

            serviceDownloadDirectory.setMap(resource);

            Path sourceZipArchive = serviceDownloadDirectory.download(
                    data.systemParents(), data.systemDirectoryName());
            StreamingResponseBody responseBody = UtilsDataApi.getStreamingResponseBodyFromPath(sourceZipArchive);

            return ResponseEntity
                    .ok()
                    .headers(UtilsDataApi.collectHeadersForZip(sourceZipArchive))
                    .body(responseBody);
        }

        /**
         * Download directory with selected file objects.
         *
         * @param data        Directory download data.
         * @param accessToken Raw JWT access token.
         * @return Zip file.
         * @throws FileObjectIsFrozenException File object is frozen.
         * @throws IOException                 Signals that an I/O exception to some sort has occurred.
         */
        @SuppressWarnings("unused")
        @AtomicFileSystem(
                name = "download-directory-select",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                isExceptionMulti = true,
                                handler = ServiceFileSystemExceptionHandlerOperationDownload.class,
                                operation = ContextAtomicFileSystem.Operations.DOWNLOAD
                        )
                }
        )
        public @NotNull ResponseEntity<StreamingResponseBody> download(
                final @NotNull DataDownloadDirectorySelectApi data, String accessToken)
                throws FileObjectIsFrozenException, IOException {
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

            Path sourceZipArchive = serviceDownloadDirectorySelect.download(data.attached());
            StreamingResponseBody responseBody = UtilsDataApi.getStreamingResponseBodyFromPath(sourceZipArchive);

            return ResponseEntity
                    .ok()
                    .headers(UtilsDataApi.collectHeadersForZip(sourceZipArchive))
                    .body(responseBody);
        }

    }

}
