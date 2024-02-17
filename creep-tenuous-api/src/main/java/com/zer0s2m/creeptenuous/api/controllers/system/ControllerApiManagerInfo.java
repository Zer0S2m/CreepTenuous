package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiManagerInfoDoc;
import com.zer0s2m.creeptenuous.api.annotation.V1APIRestController;
import com.zer0s2m.creeptenuous.common.data.DataManagerInfoApi;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.exceptions.FileObjectIsFrozenException;
import com.zer0s2m.creeptenuous.common.http.ResponseManagerInfoApi;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceManagerDirectoryRedis;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;

@V1APIRestController
public class ControllerApiManagerInfo implements ControllerApiManagerInfoDoc {

    static final OperationRights operationRights = OperationRights.SHOW;

    private final ServiceManagerDirectoryRedis serviceManagerDirectoryRedis;

    private final ServiceManagerRights serviceManagerRights;

    public ControllerApiManagerInfo(
            ServiceManagerDirectoryRedis serviceManagerDirectoryRedis,
            ServiceManagerRights serviceManagerRights) {
        this.serviceManagerDirectoryRedis = serviceManagerDirectoryRedis;
        this.serviceManagerRights = serviceManagerRights;
    }

    /**
     * Get information about file objects by system names.
     *
     * @param data        Data system names
     * @param accessToken Raw JWT access token.
     * @return Information about file objects
     * @throws FileObjectIsFrozenException file object is frozen.
     */
    @Override
    @PostMapping("/file-system-object/info")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseManagerInfoApi getInfoFileObjectsBySystemNames(
            final @Valid @RequestBody @NotNull DataManagerInfoApi data,
            @RequestHeader(name = "Authorization")  String accessToken) throws FileObjectIsFrozenException {
        serviceManagerDirectoryRedis.setAccessToken(accessToken);
        serviceManagerDirectoryRedis.setIsException(false);

        serviceManagerRights.setAccessClaims(accessToken);
        serviceManagerRights.setIsWillBeCreated(false);

        boolean isRights = serviceManagerDirectoryRedis.checkRights(data.systemNames());
        if (!isRights) {
            serviceManagerRights.checkRightsByOperation(operationRights, data.systemNames());

            boolean isFrozen = serviceManagerDirectoryRedis.isFrozenFileSystemObject(data.systemNames());
            if (isFrozen) {
                throw new FileObjectIsFrozenException();
            }
        }

        return new ResponseManagerInfoApi(
                serviceManagerDirectoryRedis.build(
                        serviceManagerRights.permissionFiltering(data.systemNames(), operationRights)
                ));
    }

}
