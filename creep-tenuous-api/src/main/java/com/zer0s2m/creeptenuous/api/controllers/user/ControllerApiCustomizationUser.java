package com.zer0s2m.creeptenuous.api.controllers.user;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiCustomizationUserDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceFileSystemRedisManagerRightsAccess;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.services.user.ServiceCustomizationUser;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@V1APIRestController
public class ControllerApiCustomizationUser implements ControllerApiCustomizationUserDoc {

    private final ServiceCustomizationUser serviceCustomizationUser;

    private final BaseServiceFileSystemRedisManagerRightsAccess baseServiceFileSystemRedis;

    private final JwtProvider jwtProvider;

    public ControllerApiCustomizationUser(
            ServiceCustomizationUser serviceCustomizationUser,
            BaseServiceFileSystemRedisManagerRightsAccess baseServiceFileSystemRedis,
            JwtProvider jwtProvider) {
        this.serviceCustomizationUser = serviceCustomizationUser;
        this.baseServiceFileSystemRedis = baseServiceFileSystemRedis;
        this.jwtProvider = jwtProvider;
    }

    @Override
    @PutMapping("/user/customization/directory/color")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void setColorInDirectory(String accessToken) {

    }

    @Override
    @DeleteMapping("/user/customization/directory/color")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteColorInDirectory(String accessToken) {

    }

}
