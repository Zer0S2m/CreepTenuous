package CreepTenuous.services.user.create.services.impl;

import CreepTenuous.services.user.create.enums.UserAlready;
import CreepTenuous.services.user.create.services.ICreateUser;
import CreepTenuous.services.user.exceptions.UserAlreadyExistException;
import CreepTenuous.models.User;
import CreepTenuous.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("create-user")
public class CreateUser implements ICreateUser {
    @Autowired
    private UserRepository userRepository;

    @Override
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
