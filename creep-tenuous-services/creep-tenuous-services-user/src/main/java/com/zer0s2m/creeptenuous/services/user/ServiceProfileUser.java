package com.zer0s2m.creeptenuous.services.user;

import com.zer0s2m.creeptenuous.common.exceptions.UploadAvatarForUserException;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.models.user.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

/**
 * Interface to implement retrieval of all information about the user and its associated objects
 */
public interface ServiceProfileUser {

    /**
     * Get user by login
     * @param login user login
     * @return user model
     */
    User getUserByLogin(String login);

    /**
     * Set setting for user - deleting a user if it is deleted
     * @param login owner user
     * @param isDelete is deleting
     * @throws UserNotFoundException not exists user
     */
    void setIsDeletingFileObjectSettings(String login, boolean isDelete) throws UserNotFoundException;

    /**
     * Set Setting - Transfer File Objects to Designated User
     * @param login owner user
     * @param transferUserId designated user for migration
     * @throws UserNotFoundException not exists user
     */
    void setTransferredUserSettings(String login, Long transferUserId) throws UserNotFoundException;

    /**
     * Set file objects to exclusions when deleting a user and then allocating them
     *
     * @param fileSystemObject system names of file objects.
     *                         Must not be {@literal null} nor must it contain {@literal null}.
     * @param login            user login. Must not be {@literal null}.
     * @throws UserNotFoundException not exists user
     */
    void setFileObjectsExclusion(Collection<UUID> fileSystemObject, String login)
            throws UserNotFoundException;

    /**
     * Remove file objects from exclusion on user deletion and then allocate them
     *
     * @param fileSystemObject system names of file objects.
     *                         Must not be {@literal null} nor must it contain {@literal null}.
     * @param login            user login. Must not be {@literal null}.
     * @throws UserNotFoundException not exists user
     */
    void deleteFileObjectsExclusion(Collection<UUID> fileSystemObject, String login)
            throws UserNotFoundException;

    /**
     * Upload an avatar for the user by his login.
     * @param file Uploaded file.
     * @param login Login user.
     * @return Title avatar.
     * @throws IOException Signals that an I/O exception to some sort has occurred.
     * @throws UploadAvatarForUserException Exceptions for loading an avatar for a user.
     */
    String uploadAvatar(final MultipartFile file, final String login)
            throws UploadAvatarForUserException, IOException;

}
