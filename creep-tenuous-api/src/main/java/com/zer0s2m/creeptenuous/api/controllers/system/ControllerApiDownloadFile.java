package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiDownloadFileDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataDownloadFile;
import com.zer0s2m.creeptenuous.common.data.DataDownloadFileApi;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;
import com.zer0s2m.creeptenuous.services.redis.system.ServiceDownloadFileRedisImpl;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceDownloadFileImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.io.IOException;
import java.util.List;

@V1APIRestController
public class ControllerApiDownloadFile implements ControllerApiDownloadFileDoc {

    static final OperationRights operationRightsDirectory = OperationRights.SHOW;

    static final OperationRights operationRightsFile = OperationRights.DOWNLOAD;

    private final ServiceDownloadFileImpl serviceDownloadFile;

    private final ServiceDownloadFileRedisImpl serviceDownloadFileRedis;

    private final ServiceManagerRights serviceManagerRights;

    @Autowired
    public ControllerApiDownloadFile(
            ServiceDownloadFileImpl serviceDownloadFile,
            ServiceDownloadFileRedisImpl serviceDownloadFileRedis,
            ServiceManagerRights serviceManagerRights
    ) {
        this.serviceDownloadFile = serviceDownloadFile;
        this.serviceDownloadFileRedis = serviceDownloadFileRedis;
        this.serviceManagerRights = serviceManagerRights;
    }

    /**
     * Download file
     * @param data file download data
     * @param accessToken raw JWT access token
     * @return file
     * @throws IOException if an I/O error occurs or the parent directory does not exist
     */
    @Override
    @PostMapping(path = "/file/download")
    public ResponseEntity<Resource> download(
            final @Valid @RequestBody DataDownloadFileApi data,
            @RequestHeader(name = "Authorization") String accessToken
    ) throws IOException {
        serviceManagerRights.setAccessClaims(accessToken);
        serviceManagerRights.setIsWillBeCreated(false);

        serviceDownloadFileRedis.setAccessToken(accessToken);
        serviceDownloadFileRedis.setIsException(false);
        boolean isRightsDirectory = serviceDownloadFileRedis.checkRights(data.parents(), data.systemParents(), null, false);

        if (!isRightsDirectory) {
            serviceManagerRights.checkRightsByOperation(operationRightsDirectory, data.systemParents());
        }

        boolean isRightsFile = serviceDownloadFileRedis.checkRights(List.of(data.systemFileName()));
        if (!isRightsFile) {
            serviceManagerRights.checkRightsByOperation(operationRightsFile, data.systemFileName());
        }

        final ContainerDataDownloadFile<ByteArrayResource, String> dataFile = serviceDownloadFile.download(
                data.systemParents(),
                data.systemFileName()
        );
        return ResponseEntity.ok()
                .headers(serviceDownloadFile.collectHeaders(dataFile))
                .body(dataFile.byteContent()
        );
    }
}