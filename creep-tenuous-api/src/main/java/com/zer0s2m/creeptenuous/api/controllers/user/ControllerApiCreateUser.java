package com.zer0s2m.creeptenuous.api.controllers.user;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiCreateUserDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.exceptions.UserAlreadyExistException;
import com.zer0s2m.creeptenuous.common.exceptions.messages.UserAlreadyExistMsg;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.services.user.ServiceCreateUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@V1APIRestController
public class ControllerApiCreateUser implements ControllerApiCreateUserDoc {

    private final ServiceCreateUser createUser;

    @Autowired
    public ControllerApiCreateUser(ServiceCreateUser createUser) {
        this.createUser = createUser;
    }

    /**
     * Creating a user in the system
     * @param user Data for creating a user in the system
     * @throws UserAlreadyExistException user already exists
     */
    @Override
    @PostMapping("/user/create")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void create(final @RequestBody User user) throws UserAlreadyExistException {
        createUser.create(user);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public UserAlreadyExistMsg handleExceptionAlready(@NotNull UserAlreadyExistException error) {
        return new UserAlreadyExistMsg(error.getMessage());
    }

}
