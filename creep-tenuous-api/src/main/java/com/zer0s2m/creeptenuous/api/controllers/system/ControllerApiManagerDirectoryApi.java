package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiManagerDirectoryApiDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataBuilderDirectory;
import com.zer0s2m.creeptenuous.common.data.DataManagerDirectoryApi;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.exceptions.NotValidLevelDirectoryException;
import com.zer0s2m.creeptenuous.common.exceptions.messages.ExceptionBadLevelDirectoryMsg;
import com.zer0s2m.creeptenuous.common.http.ResponseManagerDirectoryApi;
import com.zer0s2m.creeptenuous.common.utils.OptionalMutable;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceManagerDirectoryRedis;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceManagerDirectoryImpl;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@V1APIRestController
public class ControllerApiManagerDirectoryApi implements ControllerApiManagerDirectoryApiDoc {

    static final OperationRights operationRights = OperationRights.SHOW;

    private final ServiceManagerDirectoryImpl builderDirectory;

    private final ServiceManagerDirectoryRedis serviceManagerDirectoryRedis;

    private final ServiceManagerRights serviceManagerRights;

    @Autowired
    public ControllerApiManagerDirectoryApi(ServiceManagerDirectoryImpl builderDirectory,
                                            ServiceManagerDirectoryRedis serviceManagerDirectoryRedis,
                                            ServiceManagerRights serviceManagerRights) {
        this.builderDirectory = builderDirectory;
        this.serviceManagerDirectoryRedis = serviceManagerDirectoryRedis;
        this.serviceManagerRights = serviceManagerRights;
    }

    /**
     * Manager directory - get all directories by level
     *
     * @param data data manager directory
     * @return result manager build info in directory
     * @throws IOException                     if an I/O error occurs or the parent directory does not exist
     * @throws NotValidLevelDirectoryException invalid level directory
     */
    @Override
    @PostMapping("/directory")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseManagerDirectoryApi manager(final @Valid @RequestBody @NotNull DataManagerDirectoryApi data,
                                               @RequestHeader(name = "Authorization") String accessToken)
            throws IOException, NotValidLevelDirectoryException {
        serviceManagerDirectoryRedis.setAccessToken(accessToken);
        serviceManagerDirectoryRedis.setIsException(false);

        serviceManagerRights.setAccessClaims(accessToken);
        serviceManagerRights.setIsWillBeCreated(false);

        OptionalMutable<ContainerDataBuilderDirectory> rawDataOptional = new OptionalMutable<>();
        boolean isRights = serviceManagerDirectoryRedis.checkRights(
                data.parents(),
                data.systemParents(),
                null,
                false
        );
        if (!isRights) {
            serviceManagerRights.checkRightsByOperation(operationRights, data.systemParents());
        }

        rawDataOptional.setValue(builderDirectory.build(
                data.systemParents(),
                data.level()
        ));
        rawDataOptional.setValue(new ContainerDataBuilderDirectory(
                data.systemParents(),
                data.level(),
                serviceManagerRights.permissionFiltering(
                        rawDataOptional.getValue().namesSystemFileObject(), operationRights)
        ));

        List<Object> result = serviceManagerDirectoryRedis.build(rawDataOptional.getValue().namesSystemFileObject());

        return new ResponseManagerDirectoryApi(data.systemParents(), data.level(), result);
    }

    @ExceptionHandler(NotValidLevelDirectoryException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ExceptionBadLevelDirectoryMsg handleExceptionBadLevel(@NotNull NotValidLevelDirectoryException error) {
        return new ExceptionBadLevelDirectoryMsg(error.getMessage());
    }

}