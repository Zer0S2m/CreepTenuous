package com.zer0s2m.creeptenuous.services.user.impl;

import com.zer0s2m.creeptenuous.common.data.DataControlFileSystemObjectApi;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.models.common.ShortcutFileSystemObject;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.repository.common.ShortcutFileSystemObjectRepository;
import com.zer0s2m.creeptenuous.repository.user.UserRepository;
import com.zer0s2m.creeptenuous.services.user.ServiceShortcutFileSystemObjectsUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for interacting with labels of file objects
 */
@Service("service-shortcut-file-system-objects")
public class ServiceShortcutFileSystemObjectsUserImpl implements ServiceShortcutFileSystemObjectsUser {

    private final UserRepository userRepository;

    private final ShortcutFileSystemObjectRepository shortcutFileSystemObjectRepository;

    @Autowired
    public ServiceShortcutFileSystemObjectsUserImpl(
            UserRepository userRepository,
            ShortcutFileSystemObjectRepository shortcutFileSystemObjectRepository) {
        this.userRepository = userRepository;
        this.shortcutFileSystemObjectRepository = shortcutFileSystemObjectRepository;
    }

    /**
     * Create a shortcut to a file object
     * @param userLogin user login
     * @param attachedFileSystemObject attached file object
     * @param toAttachedFileSystemObject file object to be attached to
     * @throws UserNotFoundException user not exists
     */
    @Override
    public void create(String userLogin, UUID attachedFileSystemObject, UUID toAttachedFileSystemObject)
            throws UserNotFoundException {
        shortcutFileSystemObjectRepository.save(new ShortcutFileSystemObject(
                getUserByLogin(userLogin),
                attachedFileSystemObject,
                toAttachedFileSystemObject));
    }

    /**
     * Delete a shortcut to a file object
     * @param userLogin user login
     * @param attachedFileSystemObject attached file object
     * @param toAttachedFileSystemObject file object to be attached to
     * @throws UserNotFoundException user not exists
     */
    @Override
    @Transactional
    public void delete(String userLogin, UUID attachedFileSystemObject, UUID toAttachedFileSystemObject)
            throws UserNotFoundException {
        shortcutFileSystemObjectRepository
                .deleteByAttachedFileSystemObjectAndToAttachedFileSystemObjectAndUserLogin(
                        attachedFileSystemObject,
                        toAttachedFileSystemObject,
                        getUserByLogin(userLogin).getLogin());
    }

    /**
     * Show all shortcut to a file object
     * @param userLogin user login
     * @param toAttachedFileSystemObject file object to be attached to
     * @return file system objects
     * @throws UserNotFoundException user not exists
     */
    @Override
    public List<DataControlFileSystemObjectApi> show(String userLogin, UUID toAttachedFileSystemObject)
            throws UserNotFoundException {
        List<DataControlFileSystemObjectApi> dataControlFileSystemObjectApis = new ArrayList<>();
        shortcutFileSystemObjectRepository.getAllByToAttachedFileSystemObjectAndUserLogin(
                toAttachedFileSystemObject, getUserByLogin(userLogin).getLogin()
        ).forEach(obj -> dataControlFileSystemObjectApis.add(new DataControlFileSystemObjectApi(
                obj.getAttachedFileSystemObject().toString()
        )));
        return dataControlFileSystemObjectApis;
    }

    /**
     * Get user by login
     * @param userLogin user login
     * @return user
     * @throws UserNotFoundException user not exists
     */
    private @NotNull User getUserByLogin(String userLogin) throws UserNotFoundException {
        User user = userRepository.findByLogin(userLogin);
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user;
    }

}
