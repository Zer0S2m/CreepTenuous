package CreepTenuous.services.User.CreateUser.services.impl;

import CreepTenuous.services.User.CreateUser.enums.UserAlready;
import CreepTenuous.services.User.CreateUser.services.ICreateUser;
import CreepTenuous.services.User.exceptions.UserAlreadyExistException;
import CreepTenuous.services.User.models.User;
import CreepTenuous.services.User.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("create-user")
public class CreateUser implements ICreateUser {
    @Autowired
    private UserRepository userRepository;

    public Long create(User user) throws UserAlreadyExistException {
        Optional<String> email = Optional.ofNullable(user.getEmail());

        if (userRepository.existsUserByLogin(user.getLogin())) {
            throw new UserAlreadyExistException(
                    String.format(UserAlready.USER_ALREADY_EXISTS.get(), UserAlready.USER_LOGIN_EXISTS.get())
            );
        } else if (email.isPresent() && userRepository.existsUserByEmail(user.getEmail())) {
            throw new UserAlreadyExistException(
                    String.format(UserAlready.USER_ALREADY_EXISTS.get(), UserAlready.USER_EMAIL_EXISTS.get())
            );
        }

        userRepository.save(user);
        return user.getId();
    }
}
