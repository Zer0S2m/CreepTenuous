package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiDeleteDirectoryDoc;
import com.zer0s2m.creeptenuous.api.annotation.V1APIRestController;
import com.zer0s2m.creeptenuous.common.containers.ContainerInfoFileSystemObject;
import com.zer0s2m.creeptenuous.common.data.DataDeleteDirectoryApi;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.exceptions.FileObjectIsFrozenException;
import com.zer0s2m.creeptenuous.common.utils.CloneList;
import com.zer0s2m.creeptenuous.core.atomic.AtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.atomic.CoreServiceFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.AtomicSystemCallManager;
import com.zer0s2m.creeptenuous.core.atomic.ServiceFileSystemExceptionHandlerOperationDelete;
import com.zer0s2m.creeptenuous.core.atomic.AtomicServiceFileSystem;
import com.zer0s2m.creeptenuous.redis.events.DirectoryRedisEventPublisher;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceDeleteDirectoryRedis;
import com.zer0s2m.creeptenuous.services.system.ServiceDeleteDirectory;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceDeleteDirectoryImpl;
import com.zer0s2m.creeptenuous.common.utils.WalkDirectoryInfo;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.List;

@V1APIRestController
public class ControllerApiDeleteDirectory implements ControllerApiDeleteDirectoryDoc {

    static final OperationRights operationRightsDirectoryShow = OperationRights.SHOW;

    private final ServiceDeleteDirectory serviceDeleteDirectory = new ServiceDeleteDirectoryImpl();

    private final ServiceBuildDirectoryPath serviceBuildDirectoryPath = new ServiceBuildDirectoryPath();

    private final ServiceDeleteDirectoryRedis serviceDeleteDirectoryRedis;

    private final ServiceManagerRights serviceManagerRights;

    private final DirectoryRedisEventPublisher directoryRedisEventPublisher;

    private final AtomicFileSystemControllerApiDeleteDirectory atomicFileSystemControllerApiDeleteDirectory =
            new AtomicFileSystemControllerApiDeleteDirectory();

    @Autowired
    public ControllerApiDeleteDirectory(
            ServiceDeleteDirectoryRedis serviceDeleteDirectoryRedis,
            ServiceManagerRights serviceManagerRights,
            DirectoryRedisEventPublisher directoryRedisEventPublisher) {
        this.serviceDeleteDirectoryRedis = serviceDeleteDirectoryRedis;
        this.serviceManagerRights = serviceManagerRights;
        this.directoryRedisEventPublisher = directoryRedisEventPublisher;
    }

    /**
     * Removing a directory. Supports atomic file system mode.
     * <p>Called via {@link AtomicSystemCallManager} - {@link AtomicFileSystemControllerApiDeleteDirectory#delete(DataDeleteDirectoryApi, String)}</p>
     *
     * @param directoryForm Directory delete data.
     * @param accessToken   Raw JWT access token.
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
    @DeleteMapping("/directory/delete")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public final void deleteDirectory(final @Valid @RequestBody @NotNull DataDeleteDirectoryApi directoryForm,
                                      @RequestHeader(name = "Authorization") String accessToken)
            throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException, IOException, FileObjectIsFrozenException {
        AtomicSystemCallManager.call(
                atomicFileSystemControllerApiDeleteDirectory,
                directoryForm,
                accessToken
        );
    }

    @CoreServiceFileSystem(method = "delete")
    public final class AtomicFileSystemControllerApiDeleteDirectory implements AtomicServiceFileSystem {

        /**
         * Removing a directory.
         *
         * @param directoryForm Directory delete data.
         * @param accessToken   Raw JWT access token.
         * @throws FileObjectIsFrozenException File object is frozen.
         * @throws IOException                 Signals that an I/O exception to some sort has occurred.
         */
        @SuppressWarnings("unused")
        @AtomicFileSystem(
                name = "delete-directory",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                isExceptionMulti = true,
                                handler = ServiceFileSystemExceptionHandlerOperationDelete.class,
                                operation = ContextAtomicFileSystem.Operations.DELETE
                        )
                }
        )
        public void delete(final @NotNull DataDeleteDirectoryApi directoryForm, String accessToken)
                throws FileObjectIsFrozenException, IOException {
            serviceManagerRights.setAccessClaims(accessToken);
            serviceManagerRights.setIsWillBeCreated(false);
            serviceManagerRights.setIsDirectory(true);

            serviceDeleteDirectoryRedis.setAccessToken(accessToken);
            serviceDeleteDirectoryRedis.setEnableCheckIsNameDirectory(true);
            serviceDeleteDirectoryRedis.setIsException(false);

            boolean isRightsSystemParents = serviceDeleteDirectoryRedis.checkRights(
                    directoryForm.parents(),
                    CloneList.cloneOneLevel(directoryForm.systemParents()),
                    directoryForm.systemDirectoryName()
            );
            if (!isRightsSystemParents) {
                final List<String> cloneSystemsParents = CloneList.cloneOneLevel(directoryForm.systemParents(),
                        List.of(directoryForm.systemDirectoryName()));
                serviceManagerRights.checkRightsByOperation(operationRightsDirectoryShow, cloneSystemsParents);
                serviceManagerRights.checkRightByOperationDeleteDirectory(directoryForm.systemDirectoryName());

                boolean isFrozen = serviceDeleteDirectoryRedis.isFrozenFileSystemObject(cloneSystemsParents);
                if (isFrozen) {
                    throw new FileObjectIsFrozenException();
                }
            }

            List<ContainerInfoFileSystemObject> attached = WalkDirectoryInfo.walkDirectory(
                    Path.of(serviceBuildDirectoryPath.build(
                            CloneList.cloneOneLevel(directoryForm.systemParents(),
                                    List.of(directoryForm.systemDirectoryName())))));
            List<String> namesFileSystemObject = attached
                    .stream()
                    .map(ContainerInfoFileSystemObject::nameFileSystemObject)
                    .toList();

            serviceDeleteDirectory.delete(directoryForm.systemParents(), directoryForm.systemDirectoryName());
            serviceDeleteDirectoryRedis.delete(namesFileSystemObject);
            directoryRedisEventPublisher.publishDelete(namesFileSystemObject);
        }

    }

}
