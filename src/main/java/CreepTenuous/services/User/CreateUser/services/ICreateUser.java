package CreepTenuous.services.User.CreateUser.services;

import CreepTenuous.services.User.exceptions.UserAlreadyExistException;
import CreepTenuous.services.User.models.User;

public interface ICreateUser {
    Long create(User user) throws UserAlreadyExistException;
}
