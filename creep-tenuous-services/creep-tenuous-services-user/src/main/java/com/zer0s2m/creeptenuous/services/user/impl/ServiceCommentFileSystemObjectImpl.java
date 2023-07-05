package com.zer0s2m.creeptenuous.services.user.impl;

import com.zer0s2m.creeptenuous.common.exceptions.NotFoundCommentFileSystemObjectException;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundException;
import com.zer0s2m.creeptenuous.models.common.CommentFileSystemObject;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.repository.common.CommentFileSystemObjectRepository;
import com.zer0s2m.creeptenuous.repository.user.UserRepository;
import com.zer0s2m.creeptenuous.services.user.ServiceCommentFileSystemObject;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
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

    /**
     * Delete comment file system object
     * @param id id comment. Must not be {@literal null}.
     * @param userLogin user login. Must not be {@literal null}.
     * @throws NotFoundException not found comments for filesystem objects
     */
    @Override
    public void delete(Long id, String userLogin) throws NotFoundException {
        if (!repository.existsByIdAndUserLogin(id, userLogin)) {
            throw new NotFoundCommentFileSystemObjectException();
        }
        repository.deleteById(id);
    }

    /**
     * Edit comment file system object
     * @param comment comment for file object
     * @param id id comment. Must not be {@literal null}.
     * @param userLogin user login. Must not be {@literal null}.
     * @return updated comment
     * @throws NotFoundException not found comments for filesystem objects
     */
    @Override
    public CommentFileSystemObject edit(String comment, Long id, String userLogin) throws NotFoundException {
        Optional<CommentFileSystemObject> obj = repository.findByIdAndUserLogin(id, userLogin);
        if (obj.isEmpty()) {
            throw new NotFoundCommentFileSystemObjectException();
        }
        CommentFileSystemObject readyObj = obj.get();
        readyObj.setComment(comment);
        repository.save(readyObj);
        return readyObj;
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
