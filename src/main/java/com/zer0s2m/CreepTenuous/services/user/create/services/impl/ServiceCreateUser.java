package com.zer0s2m.CreepTenuous.services.user.create.services.impl;

import com.zer0s2m.CreepTenuous.services.user.enums.UserAlready;
import com.zer0s2m.CreepTenuous.services.user.create.services.IServiceCreateUser;
import com.zer0s2m.CreepTenuous.services.user.enums.UserRole;
import com.zer0s2m.CreepTenuous.services.user.exceptions.UserAlreadyExistException;
import com.zer0s2m.CreepTenuous.models.User;
import com.zer0s2m.CreepTenuous.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("service-create-user")
public class ServiceCreateUser implements IServiceCreateUser {
    private final UserRepository userRepository;

    @Autowired
    public ServiceCreateUser(UserRepository userRepository) {
        this.userRepository = userRepository;
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

        userRepository.save(user);
        return user.getId();
    }
}
