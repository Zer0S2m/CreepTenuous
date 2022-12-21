package CreepTenuous.api.controllers.user.createUser;

import CreepTenuous.api.core.version.v1.V1APIController;
import CreepTenuous.services.user.createUser.services.impl.CreateUser;
import CreepTenuous.services.user.exceptions.UserAlreadyExistException;
import CreepTenuous.services.user.exceptions.data.UserAlreadyExist;
import CreepTenuous.models.User;

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
