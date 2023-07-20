package com.zer0s2m.creeptenuous.services.user.impl;

import com.zer0s2m.creeptenuous.repository.user.UserCategoryRepository;
import com.zer0s2m.creeptenuous.services.user.ServiceCategoryUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Basic service for working with an entity - a category for a user
 */
@Service("service-category-user")
public class ServiceCategoryUserImpl implements ServiceCategoryUser {

    private final UserCategoryRepository userCategoryRepository;

    @Autowired
    public ServiceCategoryUserImpl(UserCategoryRepository userCategoryRepository) {
        this.userCategoryRepository = userCategoryRepository;
    }

    @Override
    public void getAll() {

    }

    @Override
    public void create() {

    }

    @Override
    public void edit() {

    }

    @Override
    public void delete() {

    }

}
