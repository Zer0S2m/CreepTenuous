package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiCopyDirectoryDoc;
import com.zer0s2m.creeptenuous.api.annotation.V1APIRestController;
import com.zer0s2m.creeptenuous.common.containers.ContainerInfoFileSystemObject;
import com.zer0s2m.creeptenuous.common.data.DataCopyDirectoryApi;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.exceptions.FileObjectIsFrozenException;
import com.zer0s2m.creeptenuous.common.http.ResponseCopyDirectoryApi;
import com.zer0s2m.creeptenuous.common.utils.CloneList;
import com.zer0s2m.creeptenuous.core.atomic.AtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.atomic.CoreServiceFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.AtomicSystemCallManager;
import com.zer0s2m.creeptenuous.core.atomic.ServiceFileSystemExceptionHandlerOperationCopy;
import com.zer0s2m.creeptenuous.core.atomic.AtomicServiceFileSystem;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceCopyDirectoryRedis;
import com.zer0s2m.creeptenuous.services.system.ServiceCopyDirectory;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceCopyDirectoryImpl;
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

@V1APIRestController
public class ControllerApiCopyDirectory implements ControllerApiCopyDirectoryDoc {

    static final OperationRights operationRightsDirectory = OperationRights.SHOW;

    private final ServiceCopyDirectory serviceCopyDirectory = new ServiceCopyDirectoryImpl();

    private final ServiceCopyDirectoryRedis serviceCopyDirectoryRedis;

    private final ServiceManagerRights serviceManagerRights;

    private final AtomicFileSystemControllerApiCopyDirectory atomicFileSystemControllerApiCopyDirectory =
            new AtomicFileSystemControllerApiCopyDirectory();

    @Autowired
    public ControllerApiCopyDirectory(
            ServiceCopyDirectoryRedis serviceCopyDirectoryRedis,
            ServiceManagerRights serviceManagerRights) {
        this.serviceCopyDirectoryRedis = serviceCopyDirectoryRedis;
        this.serviceManagerRights = serviceManagerRights;
    }

    /**
     * Copying a directory. Supports atomic file system mode.
     * <p>Called via {@link AtomicSystemCallManager} - {@link AtomicFileSystemControllerApiCopyDirectory#copy(DataCopyDirectoryApi, String)}</p>
     *
     * @param dataDirectory Directory copy data.
     * @param accessToken   Raw JWT access token.
     * @return Result copy directory.
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
    @PostMapping("/directory/copy")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseCopyDirectoryApi copyDirectory(
            final @Valid @RequestBody @NotNull DataCopyDirectoryApi dataDirectory,
            @RequestHeader(name = "Authorization") String accessToken) throws InvocationTargetException,
            NoSuchMethodException, InstantiationException, IllegalAccessException, IOException,
            FileObjectIsFrozenException {
        return AtomicSystemCallManager.call(
                atomicFileSystemControllerApiCopyDirectory,
                dataDirectory,
                accessToken
        );
    }

    @CoreServiceFileSystem(method = "copy")
    public final class AtomicFileSystemControllerApiCopyDirectory implements AtomicServiceFileSystem {

        /**
         * Copying a directory.
         *
         * @param dataDirectory Directory copy data.
         * @param accessToken   Raw JWT access token.
         * @return Result copy directory.
         * @throws FileObjectIsFrozenException File object is frozen.
         * @throws IOException                 Signals that an I/O exception to some sort has occurred.
         */
        @Contract("_, _ -> new")
        @SuppressWarnings("unused")
        @AtomicFileSystem(
                name = "copy-directory",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                isExceptionMulti = true,
                                handler = ServiceFileSystemExceptionHandlerOperationCopy.class,
                                operation = ContextAtomicFileSystem.Operations.COPY
                        )
                }
        )
        public @NotNull ResponseCopyDirectoryApi copy(
                final @NotNull DataCopyDirectoryApi dataDirectory,
                String accessToken) throws FileObjectIsFrozenException, IOException {
            serviceManagerRights.setAccessClaims(accessToken);
            serviceManagerRights.setIsWillBeCreated(false);
            serviceManagerRights.setIsDirectory(true);

            serviceCopyDirectoryRedis.setAccessToken(accessToken);
            serviceCopyDirectoryRedis.setEnableCheckIsNameDirectory(true);
            serviceCopyDirectoryRedis.setResetCheckIsNameDirectory(true);
            serviceCopyDirectoryRedis.setIsException(false);

            boolean isRightsDirectorySource = serviceCopyDirectoryRedis.checkRights(
                    dataDirectory.parents(),
                    CloneList.cloneOneLevel(dataDirectory.systemParents()),
                    dataDirectory.systemDirectoryName());
            boolean isRightsDirectoryTarget = serviceCopyDirectoryRedis.checkRights(dataDirectory.systemToParents());
            if (!isRightsDirectorySource || !isRightsDirectoryTarget) {
                if (!isRightsDirectorySource) {
                    serviceManagerRights.checkRightsByOperation(
                            operationRightsDirectory, CloneList.cloneOneLevel(dataDirectory.systemParents(),
                                    List.of(dataDirectory.systemDirectoryName())));
                }
                if (!isRightsDirectoryTarget) {
                    serviceManagerRights.checkRightsByOperation(operationRightsDirectory,
                            dataDirectory.systemToParents());
                }
                serviceManagerRights.checkRightByOperationCopyDirectory(dataDirectory.systemDirectoryName());

                boolean isFrozenFileSystemObjects = serviceCopyDirectoryRedis.isFrozenFileSystemObject(
                        CloneList.cloneOneLevel(CloneList.cloneOneLevel(
                                        dataDirectory.systemParents(), dataDirectory.systemToParents()),
                                List.of(dataDirectory.systemDirectoryName())));
                if (isFrozenFileSystemObjects) {
                    throw new FileObjectIsFrozenException();
                }
            }

            List<ContainerInfoFileSystemObject> attached = serviceCopyDirectory.copy(
                    dataDirectory.systemParents(),
                    dataDirectory.systemToParents(),
                    dataDirectory.systemDirectoryName(),
                    dataDirectory.method());

            serviceCopyDirectoryRedis.copy(attached);

            return new ResponseCopyDirectoryApi(attached);
        }

    }

}
