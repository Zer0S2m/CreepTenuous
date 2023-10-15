package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiUploadFileDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.components.SystemMode;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataUploadFile;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataUploadFileFragment;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.exceptions.FileObjectIsFrozenException;
import com.zer0s2m.creeptenuous.common.http.ResponseObjectUploadFileApi;
import com.zer0s2m.creeptenuous.common.http.ResponseUploadFileApi;
import com.zer0s2m.creeptenuous.common.utils.UtilsDataApi;
import com.zer0s2m.creeptenuous.core.atomic.handlers.AtomicSystemCallManager;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceUploadFileRedis;
import com.zer0s2m.creeptenuous.services.system.ServiceUploadFile;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceUploadFileImpl;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@V1APIRestController
public class ControllerApiUploadFile implements ControllerApiUploadFileDoc {

    static final OperationRights operationRights = OperationRights.UPLOAD;

    private final ServiceUploadFile serviceUploadFile;

    private final ServiceUploadFileRedis serviceUploadFileRedis;

    private final ServiceManagerRights serviceManagerRights;

    @Autowired
    public ControllerApiUploadFile(ServiceUploadFile serviceUploadFile,
                                   ServiceUploadFileRedis serviceUploadFileRedis,
                                   ServiceManagerRights serviceManagerRights) {
        this.serviceUploadFile = serviceUploadFile;
        this.serviceUploadFileRedis = serviceUploadFileRedis;
        this.serviceManagerRights = serviceManagerRights;
    }

    /**
     * Upload file
     * <p>Called method via {@link AtomicSystemCallManager} - {@link ServiceUploadFileImpl#upload(List, List)}</p>
     *
     * @param files         raw files
     * @param parents       real names directories
     * @param systemParents parts of the system path - source
     * @param accessToken   raw JWT access token
     * @return result upload file
     * @throws InvocationTargetException   Exception thrown by an invoked method or constructor.
     * @throws NoSuchMethodException       Thrown when a particular method cannot be found.
     * @throws InstantiationException      Thrown when an application tries to create an instance of a class
     *                                     using the newInstance method in class {@code Class}.
     * @throws IllegalAccessException      An IllegalAccessException is thrown when an application
     *                                     tries to reflectively create an instance
     * @throws FileObjectIsFrozenException file object is frozen
     */
    @Override
    @PostMapping(value = "/file/upload")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseUploadFileApi upload(
            final @RequestPart("files") List<MultipartFile> files,
            final @RequestParam("parents") List<String> parents,
            final @RequestParam("systemParents") List<String> systemParents,
            @RequestHeader(name = "Authorization") String accessToken
    ) throws InvocationTargetException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, FileObjectIsFrozenException, IOException {
        serviceUploadFileRedis.setAccessToken(accessToken);
        serviceUploadFileRedis.setIsException(false);

        boolean isRights = serviceUploadFileRedis.checkRights(parents, systemParents, null);
        if (!isRights) {
            serviceManagerRights.setAccessClaims(accessToken);
            serviceManagerRights.setIsWillBeCreated(false);
            serviceManagerRights.checkRightsByOperation(operationRights, systemParents);

            boolean isFrozen = serviceUploadFileRedis.isFrozenFileSystemObject(systemParents);
            if (isFrozen) {
                throw new FileObjectIsFrozenException();
            }
        }

        if (SystemMode.isSplitFileInUpload()) {
            uploadFragment(files, systemParents);
        }

        List<ResponseObjectUploadFileApi> data = AtomicSystemCallManager.call(
                serviceUploadFile,
                files,
                systemParents
        );
        serviceUploadFileRedis.upload(data
                .stream()
                .map((obj) -> {
                    if (obj.success()) {
                        return new ContainerDataUploadFile(
                                obj.realFileName(),
                                UtilsDataApi.clearFileExtensions(obj.systemFileName()),
                                obj.realPath(),
                                obj.systemPath()
                        );
                    }
                    return null;
                })
                .toList());

        List<ResponseObjectUploadFileApi> readyData = data
                .stream()
                .map(response -> new ResponseObjectUploadFileApi(
                        response.realFileName(),
                        UtilsDataApi.clearFileExtensions(response.systemFileName()),
                        response.success(),
                        response.realPath(),
                        response.systemPath()
                ))
                .toList();

        return new ResponseUploadFileApi(readyData);
    }

    /**
     * Upload files and then fragment them.
     * @param files Files.
     * @param systemParents System names of directories from which the directory path will be
     *                      collected where the file will be downloaded
     * @throws IOException If an I/O error occurs or the parent directory does not exist.
     */
    private void uploadFragment(
            final @NotNull List<MultipartFile> files, final List<String> systemParents)
            throws IOException {
        final List<InputStream> inputStreams = new ArrayList<>();
        final List<String> originalFileNames = new ArrayList<>();

        files.forEach(file -> {
            try {
                inputStreams.add(file.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            originalFileNames.add(file.getOriginalFilename());
        });

        List<ContainerDataUploadFileFragment> dataUploadFileFragments =
                serviceUploadFile.uploadFragment(inputStreams, originalFileNames, systemParents);

        System.out.println(dataUploadFileFragments);
    }

}
