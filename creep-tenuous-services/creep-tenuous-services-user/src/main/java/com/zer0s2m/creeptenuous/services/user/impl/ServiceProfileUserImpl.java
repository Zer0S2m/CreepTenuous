package com.zer0s2m.creeptenuous.services.user.impl;

import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.repository.user.UserRepository;
import com.zer0s2m.creeptenuous.services.user.ServiceProfileUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for getting all information about the user and his related objects
 */
@Service("service-profile-user")
public class ServiceProfileUserImpl implements ServiceProfileUser {

    private final UserRepository userRepository;

    @Autowired
    public ServiceProfileUserImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Get user by login
     * @param login user login
     * @return user model
     */
    @Override
    public User getUserByLogin(String login) {
        return userRepository.findByLogin(login);
    }

}
