package com.zer0s2m.creeptenuous.services.user.impl;

import com.zer0s2m.creeptenuous.models.common.CommentFileSystemObject;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.repository.common.CommentFileSystemObjectRepository;
import com.zer0s2m.creeptenuous.repository.user.UserRepository;
import com.zer0s2m.creeptenuous.services.user.ServiceCommentFileSystemObject;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service for interacting with comments for file system objects
 */
@Service("service-comment-file-system-object-user")
public class ServiceCommentFileSystemObjectImpl implements ServiceCommentFileSystemObject {

    private final CommentFileSystemObjectRepository repository;

    private final UserRepository userRepository;

    @Autowired
    public ServiceCommentFileSystemObjectImpl(
            CommentFileSystemObjectRepository repository,
            UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    /**
     * Create Comment for File System Object
     * @param comment Comment for file object
     * @param fileSystemObject File object name
     * @param userId user id
     * @return comment
     */
    @Override
    public CommentFileSystemObject create(String comment, String fileSystemObject, Long userId) {
        return create(comment, fileSystemObject, getUser(userId));
    }

    /**
     * Create Comment for File System Object
     * @param comment Comment for file object
     * @param fileSystemObject File object name
     * @param login user login
     * @return comment
     */
    @Override
    public CommentFileSystemObject create(String comment, String fileSystemObject, String login) {
        return create(comment, fileSystemObject, getUser(login));
    }

    /**
     * Create Comment for File System Object
     * @param comment Comment for file object
     * @param fileSystemObject File object name
     * @param user target user
     * @return comment
     */
    private @NotNull CommentFileSystemObject create(String comment, String fileSystemObject, User user) {
        final CommentFileSystemObject obj = new CommentFileSystemObject(
                user, comment, UUID.fromString(fileSystemObject));
        return repository.save(obj);
    }

    @Override
    public void delete() {

    }

    @Override
    public void edit() {

    }

    /**
     * Get user by id
     * @param id user id
     * @return user
     */
    private @NotNull User getUser(Long id) {
        return userRepository.findById(id).get();
    }

    /**
     * Get user by login
     * @param login user login
     * @return user
     */
    private User getUser(String login) {
        return userRepository.findByLogin(login);
    }

}
