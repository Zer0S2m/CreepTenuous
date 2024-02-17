package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiUploadFileDoc;
import com.zer0s2m.creeptenuous.api.annotation.V1APIRestController;
import com.zer0s2m.creeptenuous.common.components.SystemMode;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataUploadFile;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataUploadFileFragment;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.exceptions.FileObjectIsFrozenException;
import com.zer0s2m.creeptenuous.common.http.ResponseObjectUploadFileApi;
import com.zer0s2m.creeptenuous.common.http.ResponseUploadFileApi;
import com.zer0s2m.creeptenuous.common.utils.UtilsFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.AtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.atomic.CoreServiceFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.AtomicSystemCallManager;
import com.zer0s2m.creeptenuous.core.atomic.ServiceFileSystemExceptionHandlerOperationFragmentation;
import com.zer0s2m.creeptenuous.core.atomic.ServiceFileSystemExceptionHandlerOperationUpload;
import com.zer0s2m.creeptenuous.core.atomic.AtomicServiceFileSystem;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceUploadFileRedis;
import com.zer0s2m.creeptenuous.services.system.ServiceUploadFile;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceUploadFileImpl;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@V1APIRestController
public class ControllerApiUploadFile extends BaseControllerApiUploadFileObject
        implements ControllerApiUploadFileDoc {

    static final OperationRights operationRights = OperationRights.UPLOAD;

    private final ServiceUploadFile serviceUploadFile = new ServiceUploadFileImpl();

    private final ServiceUploadFileRedis serviceUploadFileRedis;

    private final ServiceManagerRights serviceManagerRights;

    private final AtomicFileSystemControllerApiUploadFile atomicFileSystemControllerApiUploadFile =
            new AtomicFileSystemControllerApiUploadFile();

    @Autowired
    public ControllerApiUploadFile(
            ServiceUploadFileRedis serviceUploadFileRedis,
            ServiceManagerRights serviceManagerRights) {
        this.serviceUploadFileRedis = serviceUploadFileRedis;
        this.serviceManagerRights = serviceManagerRights;
    }

    /**
     * Uploading a file. Supports atomic file system mode.
     * <p>Called method via {@link AtomicSystemCallManager} - {@link AtomicFileSystemControllerApiUploadFile#upload(List, List, List, String)} </p>
     *
     * @param files         Raw files.
     * @param parents       Real names directories.
     * @param systemParents Parts of the system path - source.
     * @param accessToken   Raw JWT access token.
     * @return Result upload file.
     * @throws InvocationTargetException   Exception thrown by an invoked method or constructor.
     * @throws NoSuchMethodException       Thrown when a particular method cannot be found.
     * @throws InstantiationException      Thrown when an application tries to create an instance of a class
     *                                     using the newInstance method in class {@code Class}.
     * @throws IllegalAccessException      An IllegalAccessException is thrown when an application
     *                                     tries to reflectively create an instance.
     * @throws FileObjectIsFrozenException File object is frozen.
     * @throws IOException                 Signals that an I/O exception to some sort has occurred.
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
        return AtomicSystemCallManager.call(
                atomicFileSystemControllerApiUploadFile,
                files,
                parents,
                systemParents,
                accessToken
        );
    }

    /**
     * Upload files and then fragment them.
     *
     * @param files         Files.
     * @param systemParents System names of directories from which the directory path will be
     *                      collected where the file will be downloaded.
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

        serviceUploadFileRedis.uploadFragment(dataUploadFileFragments);
    }

    @CoreServiceFileSystem(method = "upload")
    public final class AtomicFileSystemControllerApiUploadFile implements AtomicServiceFileSystem {

        /**
         * Uploading a file.
         *
         * @param files         Raw files.
         * @param parents       Real names directories.
         * @param systemParents Parts of the system path - source.
         * @param accessToken   Raw JWT access token.
         * @return Result upload file.
         * @throws FileObjectIsFrozenException File object is frozen.
         * @throws IOException                 Signals that an I/O exception to some sort has occurred.
         */
        @Contract("_, _, _, _ -> new")
        @SuppressWarnings("unused")
        @AtomicFileSystem(
                name = "upload-file",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                isExceptionMulti = true,
                                handler = ServiceFileSystemExceptionHandlerOperationUpload.class,
                                operation = ContextAtomicFileSystem.Operations.UPLOAD
                        ),
                        @AtomicFileSystemExceptionHandler(
                                isExceptionMulti = true,
                                handler = ServiceFileSystemExceptionHandlerOperationFragmentation.class,
                                operation = ContextAtomicFileSystem.Operations.FRAGMENTATION
                        )
                }
        )
        public @NotNull ResponseUploadFileApi upload(
                final List<MultipartFile> files,
                final List<String> parents,
                final List<String> systemParents,
                String accessToken) throws FileObjectIsFrozenException, IOException {
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

            // Use file fragmentation during development to obtain more detailed information
            if (SystemMode.isSplitModeFragmentFile()) {
                uploadFragment(files, systemParents);
            }

            Map<Path, String> dataFiles = new HashMap<>();
            files.forEach(file -> {
                try {
                    dataFiles.put(transfer(file), file.getOriginalFilename());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            List<ResponseObjectUploadFileApi> data = serviceUploadFile.upload(
                    dataFiles, systemParents);

            serviceUploadFileRedis.upload(data
                    .stream()
                    .map((obj) -> {
                        if (obj.success()) {
                            return new ContainerDataUploadFile(
                                    obj.realFileName(),
                                    UtilsFileSystem.clearFileExtensions(obj.systemFileName()),
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
                            UtilsFileSystem.clearFileExtensions(response.systemFileName()),
                            response.success(),
                            response.realPath(),
                            response.systemPath()
                    ))
                    .toList();

            return new ResponseUploadFileApi(readyData);
        }

    }

}
