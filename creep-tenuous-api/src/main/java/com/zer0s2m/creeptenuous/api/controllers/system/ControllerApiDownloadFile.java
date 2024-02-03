package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiDownloadFileDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.components.SystemMode;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataDownloadFile;
import com.zer0s2m.creeptenuous.common.data.DataDownloadFileApi;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.exceptions.FileObjectIsFrozenException;
import com.zer0s2m.creeptenuous.common.utils.UtilsDataApi;
import com.zer0s2m.creeptenuous.core.balancer.FileIsDirectoryException;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.services.resources.ServiceRedisManagerResources;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceFileSystemRedisManagerRightsAccess;
import com.zer0s2m.creeptenuous.services.system.ServiceDownloadFile;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceDownloadFileImpl;
import com.zer0s2m.creeptenuous.services.system.utils.UtilsFiles;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@V1APIRestController
public class ControllerApiDownloadFile implements ControllerApiDownloadFileDoc {

    static final OperationRights operationRightsDirectory = OperationRights.SHOW;

    static final OperationRights operationRightsFile = OperationRights.DOWNLOAD;

    private final ServiceDownloadFile serviceDownloadFile = new ServiceDownloadFileImpl();

    private final BaseServiceFileSystemRedisManagerRightsAccess baseServiceFileSystemRedis;

    private final ServiceRedisManagerResources serviceRedisManagerResources;

    private final ServiceManagerRights serviceManagerRights;

    @Autowired
    public ControllerApiDownloadFile(
            BaseServiceFileSystemRedisManagerRightsAccess baseServiceFileSystemRedis,
            ServiceRedisManagerResources serviceRedisManagerResources,
            ServiceManagerRights serviceManagerRights) {
        this.baseServiceFileSystemRedis = baseServiceFileSystemRedis;
        this.serviceRedisManagerResources = serviceRedisManagerResources;
        this.serviceManagerRights = serviceManagerRights;
    }

    /**
     * Download file
     *
     * @param data        file download data
     * @param accessToken raw JWT access token
     * @return file
     * @throws IOException                 if an I/O error occurs or the parent directory does not exist
     * @throws FileObjectIsFrozenException file object is frozen
     */
    @Override
    @PostMapping(path = "/file/download")
    public ResponseEntity<StreamingResponseBody> download(
            final @Valid @RequestBody @NotNull DataDownloadFileApi data,
            @RequestHeader(name = "Authorization") String accessToken)
            throws IOException, FileObjectIsFrozenException, FileIsDirectoryException {
        serviceManagerRights.setAccessClaims(accessToken);
        serviceManagerRights.setIsWillBeCreated(false);

        baseServiceFileSystemRedis.setAccessToken(accessToken);
        baseServiceFileSystemRedis.setIsException(false);
        boolean isRightsDirectory = baseServiceFileSystemRedis.checkRights(data.systemParents());

        if (!isRightsDirectory) {
            serviceManagerRights.checkRightsByOperation(operationRightsDirectory, data.systemParents());

            boolean isFrozen = baseServiceFileSystemRedis.isFrozenFileSystemObject(data.systemParents());
            if (isFrozen) {
                throw new FileObjectIsFrozenException();
            }
        }

        boolean isRightsFile = baseServiceFileSystemRedis.checkRights(List.of(data.systemFileName()));
        if (!isRightsFile) {
            serviceManagerRights.checkRightsByOperation(operationRightsFile, data.systemFileName());

            boolean isFrozen = baseServiceFileSystemRedis.isFrozenFileSystemObject(data.systemFileName());
            if (isFrozen) {
                throw new FileObjectIsFrozenException();
            }
        }

        String systemFileName = data.systemFileName();

        FileRedis fileRedis = serviceRedisManagerResources.getResourceFileRedis(data.systemFileName());
        if (fileRedis != null) {
            systemFileName = UtilsFiles.getNameFileRawStr(fileRedis.getPath());
        }

        // Use file fragmentation during development to obtain more detailed information
        if (SystemMode.isSplitModeFragmentFile()) {
            return downloadFragment(data.systemParents(), systemFileName);
        }

        final ContainerDataDownloadFile<Path, String> dataFile = serviceDownloadFile
                .download(data.systemParents(), systemFileName);

        return ResponseEntity.ok()
                .headers(UtilsDataApi.collectHeadersForFile(dataFile))
                .body(UtilsDataApi.getStreamingResponseBodyFromPath(dataFile.byteContent()));
    }

    /**
     * Download a file that is fragmented.
     *
     * @param systemParents System names of directories from which the directory path will be
     *                      collected where the file.
     * @param systemName    System name of the file object.
     * @return Stream to download a file.
     */
    private @NotNull ResponseEntity<StreamingResponseBody> downloadFragment(
            final List<String> systemParents, final String systemName
    ) throws IOException, FileIsDirectoryException {
        final ContainerDataDownloadFile<Path, String> dataFile = serviceDownloadFile
                .downloadFragment(systemParents, systemName);

        return ResponseEntity.ok()
                .body(UtilsDataApi.getStreamingResponseBodyFromPath(dataFile.byteContent()));
    }

}
