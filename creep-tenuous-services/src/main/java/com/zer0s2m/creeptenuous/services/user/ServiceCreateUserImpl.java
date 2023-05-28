package com.zer0s2m.creeptenuous.services.user;

import com.zer0s2m.creeptenuous.common.exceptions.UserAlreadyExistException;
import com.zer0s2m.creeptenuous.repository.user.UserRepository;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.services.security.GeneratePasswordImpl;
import com.zer0s2m.creeptenuous.common.enums.UserRole;
import com.zer0s2m.creeptenuous.common.enums.UserAlready;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("service-create-user")
public class ServiceCreateUserImpl implements ServiceCreateUser {
    private final UserRepository userRepository;

    private final GeneratePasswordImpl generatePassword;

    @Autowired
    public ServiceCreateUserImpl(UserRepository userRepository, GeneratePasswordImpl generatePassword) {
        this.userRepository = userRepository;
        this.generatePassword = generatePassword;
    }

    /**
     * Create user in system
     * @param user entity user model
     * @return id user
     * @throws UserAlreadyExistException user already exists (by email or login)
     */
    @Override
    public Long create(User user) throws UserAlreadyExistException {
        Optional<String> email = Optional.ofNullable(user.getEmail());

        if (userRepository.existsUserByLogin(user.getLogin())) {
            throw new UserAlreadyExistException(
                    String.format(UserAlready.USER_ALREADY_EXISTS.get(), UserAlready.USER_LOGIN_EXISTS.get())
            );
        }
        if (email.isPresent() && userRepository.existsUserByEmail(user.getEmail())) {
            throw new UserAlreadyExistException(
                    String.format(UserAlready.USER_ALREADY_EXISTS.get(), UserAlready.USER_EMAIL_EXISTS.get())
            );
        }

        if (user.getRole() == null) {
            user.setRole(UserRole.ROLE_USER);
        }

        user.setPassword(generatePassword.generation(user.getPassword()));

        userRepository.save(user);
        return user.getId();
    }
}
