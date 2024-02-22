package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiManagerDirectoryDoc;
import com.zer0s2m.creeptenuous.api.annotation.V1APIRestController;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataBuilderDirectory;
import com.zer0s2m.creeptenuous.common.data.DataManagerDirectoryApi;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.exceptions.FileObjectIsFrozenException;
import com.zer0s2m.creeptenuous.common.exceptions.NotValidLevelDirectoryException;
import com.zer0s2m.creeptenuous.common.http.ResponseError;
import com.zer0s2m.creeptenuous.common.http.ResponseManagerDirectoryApi;
import com.zer0s2m.creeptenuous.common.utils.OptionalMutable;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceManagerDirectoryRedis;
import com.zer0s2m.creeptenuous.core.services.ServiceManagerDirectory;
import com.zer0s2m.creeptenuous.core.services.impl.ServiceManagerDirectoryImpl;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@V1APIRestController
public class ControllerApiManagerDirectory implements ControllerApiManagerDirectoryDoc {

    static final OperationRights operationRights = OperationRights.SHOW;

    private final ServiceManagerDirectory builderDirectory = new ServiceManagerDirectoryImpl();

    private final ServiceManagerDirectoryRedis serviceManagerDirectoryRedis;

    private final ServiceManagerRights serviceManagerRights;

    @Autowired
    public ControllerApiManagerDirectory(
            ServiceManagerDirectoryRedis serviceManagerDirectoryRedis,
            ServiceManagerRights serviceManagerRights) {
        this.serviceManagerDirectoryRedis = serviceManagerDirectoryRedis;
        this.serviceManagerRights = serviceManagerRights;
    }

    /**
     * Manager directory - get all directories by level
     *
     * @param data        data manager directory
     * @param accessToken Raw JWT access token
     * @return result manager build info in directory
     * @throws IOException                     if an I/O error occurs or the parent directory does not exist
     * @throws NotValidLevelDirectoryException invalid level directory
     * @throws FileObjectIsFrozenException     file object is frozen
     */
    @Override
    @PostMapping("/directory")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseManagerDirectoryApi manager(final @Valid @RequestBody @NotNull DataManagerDirectoryApi data,
                                               @RequestHeader(name = "Authorization") String accessToken)
            throws IOException, NotValidLevelDirectoryException, FileObjectIsFrozenException {
        serviceManagerDirectoryRedis.setAccessToken(accessToken);
        serviceManagerDirectoryRedis.setIsException(false);

        serviceManagerRights.setAccessClaims(accessToken);
        serviceManagerRights.setIsWillBeCreated(false);

        boolean isRights = serviceManagerDirectoryRedis.checkRights(data.systemParents());
        if (!isRights) {
            serviceManagerRights.checkRightsByOperation(operationRights, data.systemParents());

            boolean isFrozen = serviceManagerDirectoryRedis.isFrozenFileSystemObject(data.systemParents());
            if (isFrozen) {
                throw new FileObjectIsFrozenException();
            }
        }

        OptionalMutable<ContainerDataBuilderDirectory> rawDataOptional = new OptionalMutable<>();
        rawDataOptional.setValue(builderDirectory.build(
                data.systemParents(),
                data.level()
        ));
        System.out.println(rawDataOptional.getValue());
        rawDataOptional.setValue(new ContainerDataBuilderDirectory(
                data.systemParents(),
                data.level(),
                serviceManagerRights.permissionFiltering(
                        rawDataOptional.getValue().namesSystemFileObject(), operationRights)
        ));
        System.out.println(rawDataOptional.getValue());

        List<Object> result = serviceManagerDirectoryRedis.build(rawDataOptional.getValue().namesSystemFileObject());

        return new ResponseManagerDirectoryApi(data.parents(), data.systemParents(), data.level(), result);
    }

    @ExceptionHandler(NotValidLevelDirectoryException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ResponseError handleExceptionBadLevel(@NotNull NotValidLevelDirectoryException error) {
        return new ResponseError(error.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

}