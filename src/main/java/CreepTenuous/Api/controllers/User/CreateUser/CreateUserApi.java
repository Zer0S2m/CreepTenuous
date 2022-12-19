package CreepTenuous.Api.controllers.User.CreateUser;

import CreepTenuous.Api.core.version.v1.V1APIController;
import CreepTenuous.services.User.CreateUser.services.impl.CreateUser;
import CreepTenuous.services.User.exceptions.UserAlreadyExistException;
import CreepTenuous.services.User.exceptions.data.UserAlreadyExist;
import CreepTenuous.services.User.models.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@V1APIController
public class CreateUserApi {
    @Autowired
    private CreateUser createUser;

    @PostMapping("/user/create")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void create(
            @RequestBody User user
    ) throws UserAlreadyExistException {
        createUser.create(user);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public UserAlreadyExist handleExceptionAlready(UserAlreadyExistException error) {
        return new UserAlreadyExist(error.getMessage());
    }
}
