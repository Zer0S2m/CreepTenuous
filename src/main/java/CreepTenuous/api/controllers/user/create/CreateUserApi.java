package CreepTenuous.api.controllers.user.create;

import CreepTenuous.api.core.version.v1.V1APIController;
import CreepTenuous.services.user.create.services.impl.CreateUser;
import CreepTenuous.services.user.exceptions.UserAlreadyExistException;
import CreepTenuous.services.user.exceptions.messages.UserAlreadyExistMsg;
import CreepTenuous.models.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@V1APIController
public class CreateUserApi {
    private final CreateUser createUser;

    @Autowired
    public CreateUserApi(CreateUser createUser) {
        this.createUser = createUser;
    }

    @PostMapping("/user/create")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void create(
            final @RequestBody User user
    ) throws UserAlreadyExistException {
        createUser.create(user);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public UserAlreadyExistMsg handleExceptionAlready(UserAlreadyExistException error) {
        return new UserAlreadyExistMsg(error.getMessage());
    }
}
