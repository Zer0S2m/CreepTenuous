package com.zer0s2m.creeptenuous.api.controllers.user;

import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.http.ResponseUserApi;
import com.zer0s2m.creeptenuous.security.jwt.domain.JwtAuthentication;
import com.zer0s2m.creeptenuous.security.jwt.services.JwtService;
import org.springframework.web.bind.annotation.GetMapping;

@V1APIRestController
public class ControllerApiProfileUser {

    private final JwtService jwtService;

    public ControllerApiProfileUser(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @GetMapping("/user/profile")
    public ResponseUserApi profile() {
        JwtAuthentication userInfo = jwtService.getAuthInfo();

        return new ResponseUserApi(userInfo.getLogin() ,null, null, null);
    }

}
