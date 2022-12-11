package CreepTenuous.Api.controllers.User.CreateUser;

import CreepTenuous.Api.core.version.v1.V1APIController;
import CreepTenuous.services.User.CreateUser.services.impl.CreateUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@V1APIController
public class CreateUserApi {
    @Autowired
    private CreateUser createUser;

    @PostMapping("/user/create")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void create() {
        createUser.create();
    }
}
