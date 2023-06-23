package com.zer0s2m.creeptenuous.api.controllers.user;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiProfileUserDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.http.ResponseUserApi;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.security.jwt.domain.JwtAuthentication;
import com.zer0s2m.creeptenuous.security.jwt.services.JwtService;
import com.zer0s2m.creeptenuous.services.user.ServiceProfileUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Set;

@V1APIRestController
public class ControllerApiProfileUser implements ControllerApiProfileUserDoc {

    private final JwtService jwtService;

    private final ServiceProfileUser serviceProfileUser;

    @Autowired
    public ControllerApiProfileUser(JwtService jwtService, ServiceProfileUser serviceProfileUser) {
        this.jwtService = jwtService;
        this.serviceProfileUser = serviceProfileUser;
    }

    /**
     * Get info user by JWT token
     * @return user info
     */
    @Override
    @GetMapping("/user/profile")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseUserApi profile() {
        JwtAuthentication userInfo = jwtService.getAuthInfo();
        User currentUser = serviceProfileUser.getUserByLogin(userInfo.getLogin());
        return new ResponseUserApi(currentUser.getLogin() ,currentUser.getEmail(),
                currentUser.getName(), Set.of(currentUser.getRole()));
    }

}
