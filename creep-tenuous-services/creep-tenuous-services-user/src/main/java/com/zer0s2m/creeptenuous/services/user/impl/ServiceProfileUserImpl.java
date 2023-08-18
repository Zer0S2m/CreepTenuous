package com.zer0s2m.creeptenuous.services.user.impl;

import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.models.user.UserFileObjectsExclusion;
import com.zer0s2m.creeptenuous.models.user.UserSettings;
import com.zer0s2m.creeptenuous.repository.user.UserFileObjectsExclusionRepository;
import com.zer0s2m.creeptenuous.repository.user.UserRepository;
import com.zer0s2m.creeptenuous.repository.user.UserSettingsRepository;
import com.zer0s2m.creeptenuous.services.user.ServiceProfileUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for getting all information about the user and his related objects
 */
@Service("service-profile-user")
public class ServiceProfileUserImpl implements ServiceProfileUser {

    private final UserRepository userRepository;

    private final UserSettingsRepository userSettingsRepository;

    private final UserFileObjectsExclusionRepository userFileObjectsExclusionRepository;

    @Autowired
    public ServiceProfileUserImpl(
            UserRepository userRepository,
            UserSettingsRepository userSettingsRepository,
            UserFileObjectsExclusionRepository userFileObjectsExclusionRepository) {
        this.userRepository = userRepository;
        this.userSettingsRepository = userSettingsRepository;
        this.userFileObjectsExclusionRepository = userFileObjectsExclusionRepository;
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

    /**
     * Set setting for user - deleting a user if it is deleted
     * @param login owner user
     * @param isDelete is deleting
     * @throws UserNotFoundException not exists user
     */
    @Override
    public void setIsDeletingFileObjectSettings(String login, boolean isDelete) throws UserNotFoundException {
        User currentUser = userRepository.findByLogin(login);
        if (currentUser == null) {
            throw new UserNotFoundException();
        }

        Optional<UserSettings> userSettings = userSettingsRepository.findByUser_Login(login);
        if (userSettings.isEmpty()) {
            UserSettings newUserSettings = new UserSettings(currentUser, isDelete);
            userSettingsRepository.save(newUserSettings);
        } else {
            UserSettings cleanUserSettings = userSettings.get();
            cleanUserSettings.setIsDeletingFileObjects(isDelete);
            userSettingsRepository.save(cleanUserSettings);
        }
    }

    /**
     * Set setting - transfer file objects to designated user
     * @param login owner user
     * @param transferUserId designated user for migration
     * @throws UserNotFoundException not exists user
     */
    @Override
    public void setTransferredUserSettings(String login, Long transferUserId) throws UserNotFoundException {
        User currentUser = userRepository.findByLogin(login);
        if (currentUser == null) {
            throw new UserNotFoundException();
        }

        User transferredUser;
        if (transferUserId != null) {
            Optional<User> cleanTransferredUser = userRepository.findById(transferUserId);
            if (cleanTransferredUser.isEmpty()) {
                throw new UserNotFoundException();
            }
            transferredUser = cleanTransferredUser.get();
        } else {
            transferredUser = null;
        }

        Optional<UserSettings> userSettings = userSettingsRepository.findByUser_Login(login);
        if (userSettings.isEmpty()) {
            UserSettings newUserSettings = new UserSettings(currentUser, transferredUser);
            userSettingsRepository.save(newUserSettings);
        } else {
            UserSettings cleanUserSettings = userSettings.get();
            cleanUserSettings.setTransferredUser(transferredUser);
            userSettingsRepository.save(cleanUserSettings);
        }
    }

    /**
     * Set file objects to exclusions when deleting a user and then allocating them
     *
     * @param fileSystemObject system names of file objects.
     *                         Must not be {@literal null} nor must it contain {@literal null}.
     * @param login            user login. Must not be {@literal null}.
     * @throws UserNotFoundException not exists user
     */
    @Override
    public void setFileObjectsExclusion(Collection<UUID> fileSystemObject, String login)
            throws UserNotFoundException {
        User user = getUserByLogin(login);
        if (user == null) {
            throw new UserNotFoundException();
        }

        Collection<UserFileObjectsExclusion> fileObjectsExclusions = new ArrayList<>();
        fileSystemObject.forEach(fileObject -> fileObjectsExclusions.add(
                new UserFileObjectsExclusion(fileObject, user)
        ));
        userFileObjectsExclusionRepository.saveAll(fileObjectsExclusions);
    }

    /**
     * Remove file objects from exclusion on user deletion and then allocate them
     *
     * @param fileSystemObject system names of file objects.
     *                         Must not be {@literal null} nor must it contain {@literal null}.
     * @param login            user login. Must not be {@literal null}.
     * @throws UserNotFoundException not exists user
     */
    @Override
    @Transactional
    public void deleteFileObjectsExclusion(Collection<UUID> fileSystemObject, String login)
            throws UserNotFoundException {
        User user = getUserByLogin(login);
        if (user == null) {
            throw new UserNotFoundException();
        }

        userFileObjectsExclusionRepository.deleteAllByFileSystemObjectInAndUserLogin(
                fileSystemObject, user.getLogin());
    }

}
