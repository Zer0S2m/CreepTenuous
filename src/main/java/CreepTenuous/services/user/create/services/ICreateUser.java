package CreepTenuous.services.user.create.services;

import CreepTenuous.services.user.exceptions.UserAlreadyExistException;
import CreepTenuous.models.User;

public interface ICreateUser {
    Long create(User user) throws UserAlreadyExistException;
}
