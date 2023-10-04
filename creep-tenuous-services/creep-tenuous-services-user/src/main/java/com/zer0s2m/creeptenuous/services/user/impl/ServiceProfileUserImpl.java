package com.zer0s2m.creeptenuous.services.user.impl;

import com.zer0s2m.creeptenuous.common.components.UploadAvatar;
import com.zer0s2m.creeptenuous.common.exceptions.UploadAvatarForUserException;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.models.user.UserFileObjectsExclusion;
import com.zer0s2m.creeptenuous.models.user.UserSettings;
import com.zer0s2m.creeptenuous.repository.user.UserFileObjectsExclusionRepository;
import com.zer0s2m.creeptenuous.repository.user.UserRepository;
import com.zer0s2m.creeptenuous.repository.user.UserSettingsRepository;
import com.zer0s2m.creeptenuous.services.user.ServiceProfileUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Service for getting all information about the user and his related objects
 */
@Service("service-profile-user")
public class ServiceProfileUserImpl implements ServiceProfileUser {

    private final UserRepository userRepository;

    private final UserSettingsRepository userSettingsRepository;

    private final UserFileObjectsExclusionRepository userFileObjectsExclusionRepository;

    private final UploadAvatar uploadAvatar;

    @Autowired
    public ServiceProfileUserImpl(
            UserRepository userRepository,
            UserSettingsRepository userSettingsRepository,
            UserFileObjectsExclusionRepository userFileObjectsExclusionRepository,
            UploadAvatar uploadAvatar) {
        this.userRepository = userRepository;
        this.userSettingsRepository = userSettingsRepository;
        this.userFileObjectsExclusionRepository = userFileObjectsExclusionRepository;
        this.uploadAvatar = uploadAvatar;
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

    /**
     * Upload an avatar for the user by his login.
     * @param file Uploaded file.
     * @param login Login user.
     * @return Title avatar.
     * @throws IOException Signals that an I/O exception to some sort has occurred.
     * @throws UploadAvatarForUserException Exceptions for loading an avatar for a user.
     * @throws UserNotFoundException The user does not exist in the system.
     */
    @Override
    public String uploadAvatar(final @NotNull MultipartFile file, final String login)
            throws UploadAvatarForUserException, IOException, UserNotFoundException {
        User user = userRepository.findByLogin(login);
        if (user == null) {
            throw new UserNotFoundException();
        }
        if (user.getAvatar() != null) {
            deleteAvatar(login);
        }

        Path avatar = uploadAvatar(file);
        try {
            userRepository.updateAvatar(avatar.toString(), login);
            return avatar.getFileName().toString();
        } catch (Exception ignore) {
            Files.deleteIfExists(avatar);
            return "ERROR";
        }
    }

    /**
     * Upload the file to the specified directory {@link UploadAvatar#getUploadAvatarDir()}.
     * @param file Uploaded file.
     * @return Path.
     * @throws IOException Signals that an I/O exception to some sort has occurred.
     * @throws UploadAvatarForUserException Exceptions for loading an avatar for a user.
     */
    private @NotNull Path uploadAvatar(final @NotNull MultipartFile file)
            throws IOException, UploadAvatarForUserException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        if(fileName.contains("..")) {
            throw new UploadAvatarForUserException("Sorry! Filename contains invalid path sequence " + fileName);
        }

        String[] fileNamePart = fileName.split("\\.");
        final Path dirAvatars = Path.of(uploadAvatar.getUploadAvatarDir());
        final Path targetAvatar = dirAvatars.resolve(
                UUID.randomUUID() + "." + fileNamePart[fileNamePart.length - 1]);

        if (!Files.exists(dirAvatars)) {
            Files.createDirectories(dirAvatars);
        }

        Files.copy(file.getInputStream(), targetAvatar);

        return targetAvatar;
    }

    /**
     * Removing an avatar for a user.
     * @param login User login.
     * @throws UserNotFoundException he user does not exist in the system.
     * @throws IOException Signals that an I/O exception to some sort has occurred.
     */
    @Override
    public void deleteAvatar(final String login) throws UserNotFoundException, IOException {
        User user = userRepository.findByLogin(login);
        if (user == null) {
            throw new UserNotFoundException();
        }

        if (user.getAvatar() != null) {
            Files.deleteIfExists(Path.of(user.getAvatar()));
        }

        userRepository.updateAvatar(null, login);
    }

}
