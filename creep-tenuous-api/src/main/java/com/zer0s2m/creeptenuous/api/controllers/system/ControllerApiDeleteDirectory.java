package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiDeleteDirectoryDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.data.DataDeleteDirectoryApi;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.utils.CloneList;
import com.zer0s2m.creeptenuous.core.handlers.AtomicSystemCallManager;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceDeleteDirectoryRedis;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceDeleteDirectoryImpl;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@V1APIRestController
public class ControllerApiDeleteDirectory implements ControllerApiDeleteDirectoryDoc {

    static final OperationRights operationRightsDirectoryShow = OperationRights.SHOW;

    static final OperationRights operationRightsDirectoryDelete = OperationRights.DELETE;

    private final ServiceDeleteDirectoryImpl serviceDeleteDirectory;

    private final ServiceDeleteDirectoryRedis serviceDeleteDirectoryRedis;

    private final ServiceManagerRights serviceManagerRights;

    @Autowired
    public ControllerApiDeleteDirectory(ServiceDeleteDirectoryImpl serviceDeleteDirectory,
                                        ServiceDeleteDirectoryRedis serviceDeleteDirectoryRedis,
                                        ServiceManagerRights serviceManagerRights) {
        this.serviceDeleteDirectory = serviceDeleteDirectory;
        this.serviceDeleteDirectoryRedis = serviceDeleteDirectoryRedis;
        this.serviceManagerRights = serviceManagerRights;
    }

    /**
     * Delete directory
     * <p>Called method via {@link AtomicSystemCallManager} - {@link ServiceDeleteDirectoryImpl#delete(List, String)}</p>
     *
     * @param directoryForm directory delete data
     * @param accessToken   raw JWT access token
     * @throws InvocationTargetException Exception thrown by an invoked method or constructor.
     * @throws NoSuchMethodException     Thrown when a particular method cannot be found.
     * @throws InstantiationException    Thrown when an application tries to create an instance of a class
     *                                   using the newInstance method in class {@code Class}.
     * @throws IllegalAccessException    An IllegalAccessException is thrown when an application
     *                                   tries to reflectively create an instance
     */
    @Override
    @DeleteMapping("/directory/delete")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public final void deleteDirectory(final @Valid @RequestBody @NotNull DataDeleteDirectoryApi directoryForm,
                                      @RequestHeader(name = "Authorization") String accessToken)
            throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        serviceManagerRights.setAccessClaims(accessToken);
        serviceManagerRights.setIsWillBeCreated(false);

        serviceDeleteDirectoryRedis.setAccessToken(accessToken);
        serviceDeleteDirectoryRedis.setEnableCheckIsNameDirectory(true);
        serviceDeleteDirectoryRedis.setIsException(false);

        boolean isRightsSystemParents = serviceDeleteDirectoryRedis.checkRights(
                directoryForm.parents(),
                CloneList.cloneOneLevel(directoryForm.systemParents()),
                directoryForm.systemDirectoryName(),
                false
        );
        if (!isRightsSystemParents) {
            serviceManagerRights.checkRightsByOperation(operationRightsDirectoryShow,
                    CloneList.cloneOneLevel(directoryForm.systemParents(),
                            List.of(directoryForm.systemDirectoryName())));
            serviceManagerRights.checkRightsByOperation(operationRightsDirectoryDelete,
                    directoryForm.systemDirectoryName());
        }

        AtomicSystemCallManager.call(
                serviceDeleteDirectory,
                directoryForm.systemParents(),
                directoryForm.systemDirectoryName()
        );
        serviceDeleteDirectoryRedis.delete(directoryForm.systemDirectoryName());
    }

}
