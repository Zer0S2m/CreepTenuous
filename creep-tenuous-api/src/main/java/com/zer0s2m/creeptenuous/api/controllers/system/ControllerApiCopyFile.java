package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiCopyFileDoc;
import com.zer0s2m.creeptenuous.api.annotation.V1APIRestController;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataCopyFile;
import com.zer0s2m.creeptenuous.common.data.DataCopyFileApi;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.exceptions.FileObjectIsFrozenException;
import com.zer0s2m.creeptenuous.common.http.ResponseCopyFileApi;
import com.zer0s2m.creeptenuous.common.utils.CloneList;
import com.zer0s2m.creeptenuous.core.atomic.AtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.atomic.CoreServiceFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.AtomicSystemCallManager;
import com.zer0s2m.creeptenuous.core.atomic.ServiceFileSystemExceptionHandlerOperationCopy;
import com.zer0s2m.creeptenuous.core.atomic.AtomicServiceFileSystem;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceCopyFileRedis;
import com.zer0s2m.creeptenuous.services.system.ServiceCopyFile;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceCopyFileImpl;
import jakarta.validation.Valid;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@V1APIRestController
public class ControllerApiCopyFile implements ControllerApiCopyFileDoc {

    static final OperationRights operationRightsDirectory = OperationRights.SHOW;

    static final OperationRights operationRights = OperationRights.COPY;

    private final ServiceCopyFile serviceCopyFile = new ServiceCopyFileImpl();

    private final ServiceCopyFileRedis serviceCopyFileRedis;

    private final ServiceManagerRights serviceManagerRights;

    private final AtomicFileSystemControllerApiCopyFile atomicFileSystemControllerApiCopyFile =
            new AtomicFileSystemControllerApiCopyFile();

    @Autowired
    public ControllerApiCopyFile(
            ServiceCopyFileRedis serviceCopyFileRedis,
            ServiceManagerRights serviceManagerRights) {
        this.serviceCopyFileRedis = serviceCopyFileRedis;
        this.serviceManagerRights = serviceManagerRights;
    }

    /**
     * Copying files. Supports atomic file system mode.
     * <p>Called via {@link AtomicSystemCallManager} - {@link AtomicFileSystemControllerApiCopyFile#copy(DataCopyFileApi, String)}</p>
     *
     * @param file        Copy data file.
     * @param accessToken Raw JWT access token
     * @return Result copy file(s).
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
    @PostMapping("/file/copy")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseCopyFileApi copyFile(
            final @Valid @RequestBody @NotNull DataCopyFileApi file,
            @RequestHeader(name = "Authorization") String accessToken) throws IOException,
            InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException,
            FileObjectIsFrozenException {
        return AtomicSystemCallManager.call(
                atomicFileSystemControllerApiCopyFile,
                file,
                accessToken
        );
    }

    @CoreServiceFileSystem(method = "copy")
    public final class AtomicFileSystemControllerApiCopyFile implements AtomicServiceFileSystem {

        /**
         * Copying files.
         *
         * @param file        Copy data file.
         * @param accessToken Raw JWT access token
         * @return Result copy file(s).
         * @throws FileObjectIsFrozenException File object is frozen.
         * @throws IOException                 Signals that an I/O exception to some sort has occurred.
         */
        @Contract("_, _ -> new")
        @SuppressWarnings("unused")
        @AtomicFileSystem(
                name = "copy-file",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                isExceptionMulti = true,
                                handler = ServiceFileSystemExceptionHandlerOperationCopy.class,
                                operation = ContextAtomicFileSystem.Operations.COPY
                        )
                }
        )
        public @NotNull ResponseCopyFileApi copy(final @NotNull DataCopyFileApi file, String accessToken)
                throws FileObjectIsFrozenException, IOException {
            serviceManagerRights.setAccessClaims(accessToken);
            serviceManagerRights.setIsWillBeCreated(false);

            serviceCopyFileRedis.setIsException(false);
            serviceCopyFileRedis.setAccessToken(accessToken);
            List<String> mergeRealAndSystemParents = CloneList.cloneOneLevel(
                    file.systemParents(),
                    file.systemToParents()
            );
            boolean isRightsDirectory = serviceCopyFileRedis.checkRights(mergeRealAndSystemParents);

            if (!isRightsDirectory) {
                serviceManagerRights.checkRightsByOperation(operationRightsDirectory, file.systemParents());
                serviceManagerRights.checkRightsByOperation(operationRightsDirectory, file.systemToParents());

                boolean isFrozen = serviceCopyFileRedis.isFrozenFileSystemObject(
                        CloneList.cloneOneLevel(file.systemParents(), file.systemToParents()));
                if (isFrozen) {
                    throw new FileObjectIsFrozenException();
                }
            }

            if (file.systemFileName() != null) {
                boolean isRightsFile = serviceCopyFileRedis.checkRights(file.systemFileName());
                if (!isRightsFile) {
                    serviceManagerRights.checkRightsByOperation(operationRights, file.systemFileName());

                    boolean isFrozen = serviceCopyFileRedis.isFrozenFileSystemObject(file.systemFileName());
                    if (isFrozen) {
                        throw new FileObjectIsFrozenException();
                    }
                }

                ContainerDataCopyFile containerData = serviceCopyFile.copy(
                        file.systemFileName(),
                        file.systemParents(),
                        file.systemToParents());
                serviceCopyFileRedis.copy(containerData.target(), file.systemFileName(), containerData.systemFileName());
                return new ResponseCopyFileApi(List.of(containerData));
            } else {
                boolean isRightsFile = serviceCopyFileRedis.checkRights(file.systemNameFiles());
                if (!isRightsFile) {
                    serviceManagerRights.checkRightsByOperation(operationRights, file.systemNameFiles());

                    boolean isFrozen = serviceCopyFileRedis.isFrozenFileSystemObject(file.systemNameFiles());
                    if (isFrozen) {
                        throw new FileObjectIsFrozenException();
                    }
                }

                List<ContainerDataCopyFile> containersData = serviceCopyFile.copy(
                        Objects.requireNonNull(file.systemNameFiles()),
                        file.systemParents(),
                        file.systemToParents());
                serviceCopyFileRedis.copy(
                        containersData.stream().map(ContainerDataCopyFile::target).collect(Collectors.toList()),
                        Objects.requireNonNull(file.systemNameFiles()),
                        containersData.stream().map(ContainerDataCopyFile::systemFileName).collect(Collectors.toList())
                );
                return new ResponseCopyFileApi(containersData);
            }
        }

    }

}
