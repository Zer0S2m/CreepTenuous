package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiUploadDirectoryDoc;
import com.zer0s2m.creeptenuous.api.annotation.V1APIRestController;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.exceptions.FileObjectIsFrozenException;
import com.zer0s2m.creeptenuous.common.http.ResponseUploadDirectoryApi;
import com.zer0s2m.creeptenuous.core.atomic.AtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.atomic.CoreServiceFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.AtomicSystemCallManager;
import com.zer0s2m.creeptenuous.core.atomic.ServiceFileSystemExceptionHandlerOperationUpload;
import com.zer0s2m.creeptenuous.core.atomic.AtomicServiceFileSystem;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceUploadDirectoryRedis;
import com.zer0s2m.creeptenuous.services.system.ServiceUploadDirectory;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceUploadDirectoryImpl;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.List;

@V1APIRestController
public class ControllerApiUploadDirectory extends BaseControllerApiUploadFileObject
        implements ControllerApiUploadDirectoryDoc {

    static final OperationRights operationRights = OperationRights.UPLOAD;

    private final ServiceUploadDirectory serviceUploadDirectory = new ServiceUploadDirectoryImpl();

    private final ServiceUploadDirectoryRedis serviceUploadDirectoryRedis;

    private final ServiceManagerRights serviceManagerRights;

    private final AtomicFileSystemControllerApiUploadDirectory atomicFileSystemControllerApiUploadDirectory =
            new AtomicFileSystemControllerApiUploadDirectory();

    @Autowired
    public ControllerApiUploadDirectory(
            ServiceUploadDirectoryRedis serviceUploadDirectoryRedis,
            ServiceManagerRights serviceManagerRights) {
        this.serviceUploadDirectoryRedis = serviceUploadDirectoryRedis;
        this.serviceManagerRights = serviceManagerRights;
    }

    /**
     * Uploading a directory (zip archive). Supports atomic file system mode.
     * <p>Called via {@link AtomicSystemCallManager} - {@link AtomicFileSystemControllerApiUploadDirectory#upload(List, List, Path, String)} </p>
     *
     * @param parents       Real names directories.
     * @param systemParents Parts of the system path - source.
     * @param zipFile       Raw zip archive.
     * @param accessToken   Raw JWT access token.
     * @return Result upload directory (zip archive).
     * @throws InvocationTargetException   Exception thrown by an invoked method or constructor.
     * @throws NoSuchMethodException       Thrown when a particular method cannot be found.
     * @throws InstantiationException      Thrown when an application tries to create an instance of a class
     *                                     using the newInstance method in class {@code Class}.
     * @throws IllegalAccessException      An IllegalAccessException is thrown when an application
     *                                     tries to reflectively create an instance.
     * @throws IOException                 Signals that an I/O exception to some sort has occurred.
     * @throws FileObjectIsFrozenException File object is frozen.
     */
    @Override
    @PostMapping(path = "/directory/upload")
    @ResponseStatus(code = HttpStatus.CREATED)
    public final @NotNull ResponseUploadDirectoryApi upload(
            final @Nullable @RequestParam("parents") List<String> parents,
            final @Nullable @RequestParam("systemParents") List<String> systemParents,
            final @RequestPart("directory") MultipartFile zipFile,
            @RequestHeader(name = "Authorization") String accessToken
    ) throws InvocationTargetException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, IOException, FileObjectIsFrozenException {
        return AtomicSystemCallManager.call(
                atomicFileSystemControllerApiUploadDirectory,
                parents,
                systemParents,
                transfer(zipFile),
                accessToken
        );
    }

    @CoreServiceFileSystem(method = "upload")
    public final class AtomicFileSystemControllerApiUploadDirectory implements AtomicServiceFileSystem {

        /**
         * Uploading a directory (zip archive).
         *
         * @param parents       Real names directories.
         * @param systemParents Parts of the system path - source.
         * @param zipFile       Raw zip archive (source).
         * @param accessToken   Raw JWT access token.
         * @return Result upload directory (zip archive).
         * @throws FileObjectIsFrozenException File object is frozen.
         * @throws IOException                 Signals that an I/O exception to some sort has occurred.
         */
        @SuppressWarnings("unused")
        @AtomicFileSystem(
                name = "upload-directory",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                isExceptionMulti = true,
                                handler = ServiceFileSystemExceptionHandlerOperationUpload.class,
                                operation = ContextAtomicFileSystem.Operations.UPLOAD
                        )
                }
        )
        public @NotNull ResponseUploadDirectoryApi upload(
                final List<String> parents,
                final List<String> systemParents,
                final Path zipFile,
                String accessToken) throws FileObjectIsFrozenException, IOException {
            serviceUploadDirectoryRedis.setAccessToken(accessToken);
            serviceUploadDirectoryRedis.setIsException(false);
            boolean isRights = serviceUploadDirectoryRedis.checkRights(parents, systemParents, null);
            if (!isRights) {
                serviceManagerRights.setAccessClaims(accessToken);
                serviceManagerRights.setIsWillBeCreated(false);
                serviceManagerRights.checkRightsByOperation(operationRights, systemParents);

                boolean isFrozen = serviceUploadDirectoryRedis.isFrozenFileSystemObject(systemParents);
                if (isFrozen) {
                    throw new FileObjectIsFrozenException();
                }
            }

            Path systemPath = serviceUploadDirectory.getPath(systemParents);

            final ResponseUploadDirectoryApi finalData = serviceUploadDirectory.upload(
                    systemPath, zipFile);
            serviceUploadDirectoryRedis.upload(finalData.data());
            return finalData;
        }

    }

}
