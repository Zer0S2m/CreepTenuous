package com.zer0s2m.creeptenuous.services.user.impl;

import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.repository.user.UserRepository;
import com.zer0s2m.creeptenuous.services.user.ServiceControlUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for controlling users in the system.
 * <ul>
 *     <li>Getting all users on the system</li>
 *     <li>Removing a user from the system</li>
 * </ul>
 */
@Service("service-control-user")
public class ServiceControlUserImpl implements ServiceControlUser {

    private final UserRepository userRepository;

    @Autowired
    public ServiceControlUserImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Getting all users on the system
     * @return users
     */
    @Override
    public List<User> getAllUsers() {
        return userRepository
                .findAll()
                .stream()
                .toList();
    }

    /**
     * Removing a user from the system by his login
     * @param login user login
     * @throws UserNotFoundException user does not exist in the system
     */
    @Override
    public void deleteUser(String login) throws UserNotFoundException {
        final User user = userRepository.findByLogin(login);
        if (user == null) {
            throw new UserNotFoundException();
        }
        userRepository.delete(user);
    }

}
