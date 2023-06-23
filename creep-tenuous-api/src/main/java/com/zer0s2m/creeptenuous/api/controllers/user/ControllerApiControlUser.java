package com.zer0s2m.creeptenuous.api.controllers.user;

import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.http.ResponseUserApi;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@V1APIRestController
public class ControllerApiControlUser {

    @GetMapping("/user/control/list")
    @ResponseStatus(code = HttpStatus.OK)
    @RolesAllowed("ROLE_ADMIN")
    public List<ResponseUserApi> getAllUsers() {

    }

}
