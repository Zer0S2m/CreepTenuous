package com.zer0s2m.CreepTenuous.services.user.create.services.impl;

import com.zer0s2m.CreepTenuous.services.user.enums.UserAlready;
import com.zer0s2m.CreepTenuous.services.user.create.services.ICreateUser;
import com.zer0s2m.CreepTenuous.services.user.exceptions.UserAlreadyExistException;
import com.zer0s2m.CreepTenuous.models.User;
import com.zer0s2m.CreepTenuous.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CreateUser implements ICreateUser {
    private final UserRepository userRepository;

    @Autowired
    public CreateUser(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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
