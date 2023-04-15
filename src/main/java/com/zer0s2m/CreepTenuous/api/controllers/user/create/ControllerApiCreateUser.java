package com.zer0s2m.CreepTenuous.api.controllers.user.create;

import com.zer0s2m.CreepTenuous.api.core.version.v1.V1APIController;
import com.zer0s2m.CreepTenuous.services.user.create.services.impl.ServiceCreateUser;
import com.zer0s2m.CreepTenuous.services.user.exceptions.UserAlreadyExistException;
import com.zer0s2m.CreepTenuous.services.user.exceptions.messages.UserAlreadyExistMsg;
import com.zer0s2m.CreepTenuous.models.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@V1APIController
public class ControllerApiCreateUser {
    private final ServiceCreateUser createUser;

    @Autowired
    public ControllerApiCreateUser(ServiceCreateUser createUser) {
        this.createUser = createUser;
    }

    @PostMapping("/user/create")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void create(final @RequestBody User user) throws UserAlreadyExistException {
        createUser.create(user);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public UserAlreadyExistMsg handleExceptionAlready(UserAlreadyExistException error) {
        return new UserAlreadyExistMsg(error.getMessage());
    }
}
