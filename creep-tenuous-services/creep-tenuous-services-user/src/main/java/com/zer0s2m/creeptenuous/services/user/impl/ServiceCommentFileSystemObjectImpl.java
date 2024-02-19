package com.zer0s2m.creeptenuous.services.user.impl;

import com.zer0s2m.creeptenuous.common.containers.ContainerCommentFileSystemObject;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundCommentFileSystemObjectException;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundException;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.common.utils.OptionalMutable;
import com.zer0s2m.creeptenuous.models.common.CommentFileSystemObject;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.repository.common.CommentFileSystemObjectRepository;
import com.zer0s2m.creeptenuous.repository.user.UserRepository;
import com.zer0s2m.creeptenuous.services.user.ServiceCommentFileSystemObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
     * Get all comments
     * @param fileSystemObject name file system object
     * @param userLogin user login
     * @return comments
     */
    @Override
    public List<CommentFileSystemObject> list(String fileSystemObject, String userLogin) {
        return repository.findAllByFileSystemObjectAndUser_Login(UUID.fromString(fileSystemObject), userLogin);
    }

    /**
     * Collect comments into a tree structure.
     * @param comments Collection of comments.
     * @return Comments in a tree structure.
     */
    @Override
    public List<ContainerCommentFileSystemObject> collect(
            @NotNull Iterable<CommentFileSystemObject> comments) {
        List<ContainerCommentFileSystemObject> commentFileSystemObjects = new ArrayList<>();

        Map<Long, CommentFileSystemObject> idComments = new HashMap<>();
        // Key - comment ID
        // Value - Children's Collection
        Map<Long, List<Long>> commentsMap = new HashMap<>();

        comments.forEach(comment -> {
            if (comment.getParentId() != null) {
                commentsMap.computeIfAbsent(comment.getParentId(), k -> new ArrayList<>());
                commentsMap.get(comment.getParentId()).add(comment.getId());
            }
            idComments.put(comment.getId(), comment);
        });

        for (CommentFileSystemObject comment : comments) {
            if (comment.getParentId() != null) {
                continue;
            }

            if (!commentsMap.containsKey(comment.getId())) {
                commentFileSystemObjects.add(new ContainerCommentFileSystemObject(
                        comment.getId(),
                        comment.getComment(),
                        comment.getFileSystemObject(),
                        comment.getCreatedAt().toString(),
                        null,
                        new ArrayList<>()
                ));
            } else {
                commentFileSystemObjects.add(collectCommentChilds(commentsMap, idComments, comment, null));
            }
        }

        return commentFileSystemObjects;
    }

    private @NotNull ContainerCommentFileSystemObject collectCommentChilds(
            @NotNull Map<Long, List<Long>> commentsMap,
            Map<Long, CommentFileSystemObject> comments,
            @NotNull CommentFileSystemObject currentComment,
            @Nullable Long parentId) {
        ContainerCommentFileSystemObject commentFileSystemObject = new ContainerCommentFileSystemObject(
                currentComment.getId(),
                currentComment.getComment(),
                currentComment.getFileSystemObject(),
                currentComment.getCreatedAt().toString(),
                parentId,
                new ArrayList<>()
        );

        List<Long> childIds = commentsMap.get(currentComment.getId());

        childIds.forEach(childId -> {
            CommentFileSystemObject comment = comments.get(childId);

            if (commentsMap.containsKey(childId)) {
                Map<Long, List<Long>> commentsMapCopy = new HashMap<>(commentsMap);
                commentsMap.remove(childId);
                commentFileSystemObject.childs().add(
                        collectCommentChilds(commentsMapCopy, comments,comment , currentComment.getId()));
            } else {
                commentFileSystemObject.childs().add(new ContainerCommentFileSystemObject(
                        comment.getId(),
                        comment.getComment(),
                        comment.getFileSystemObject(),
                        comment.getCreatedAt().toString(),
                        comment.getParentId(),
                        new ArrayList<>()
                ));
            }
        });

        return commentFileSystemObject;
    }

    /**
     * Create Comment for File System Object
     * @param comment Comment for file object
     * @param fileSystemObject File object name
     * @param parentId The parent comment to which the new comment will be linked
     * @param userId user id
     * @return comment
     * @throws NotFoundException not found object.
     * @throws NotFoundCommentFileSystemObjectException The exception is for not found comments for
     * filesystem objects
     * @throws UserNotFoundException The user does not exist in the system.
     */
    @Override
    public CommentFileSystemObject create(
            String comment, String fileSystemObject, @Nullable Long parentId, Long userId)
            throws NotFoundException {
        return create(comment, fileSystemObject, parentId, getUser(userId));
    }

    /**
     * Create Comment for File System Object
     * @param comment Comment for file object
     * @param fileSystemObject File object name
     * @param parentId The parent comment to which the new comment will be linked
     * @param login user login
     * @return comment
     * @throws NotFoundException not found object.
     * @throws NotFoundCommentFileSystemObjectException The exception is for not found comments for
     * filesystem objects
     * @throws UserNotFoundException The user does not exist in the system.
     * @
     */
    @Override
    public CommentFileSystemObject create(
            String comment, String fileSystemObject, @Nullable Long parentId, String login)
            throws NotFoundException {
        return create(comment, fileSystemObject, parentId, getUser(login));
    }

    /**
     * Create Comment for File System Object
     * @param comment Comment for file object
     * @param fileSystemObject File object name
     * @param parentId The parent comment to which the new comment will be linked
     * @param user target user
     * @return comment
     * @throws NotFoundException not found comments for filesystem objects
     * @throws NotFoundCommentFileSystemObjectException The exception is for not found comments for
     * filesystem objects
     */
    private @NotNull CommentFileSystemObject create(
            String comment, String fileSystemObject, @Nullable Long parentId, User user)
            throws NotFoundException {
        OptionalMutable<CommentFileSystemObject> parentComment = new OptionalMutable<>(null);
        if (parentId != null) {
            Optional<CommentFileSystemObject> parentCommentFromDB = repository.findById(parentId);
            if (parentCommentFromDB.isEmpty()) {
                throw new NotFoundCommentFileSystemObjectException();
            }
            parentComment.setValue(parentCommentFromDB.get());
        }
        final CommentFileSystemObject obj = new CommentFileSystemObject(
                user,
                comment,
                UUID.fromString(fileSystemObject),
                parentComment.getValue() != null ? parentComment.getValue().getId() : null);
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
        Optional<CommentFileSystemObject> obj = repository.findByIdAndUser_Login(id, userLogin);
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
     * @throws NotFoundException The user does not exist in the system.
     */
    private @NotNull User getUser(Long id) throws NotFoundException {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException();
        }
        return user.get();
    }

    /**
     * Get user by login
     * @param login user login
     * @return user
     * @throws UserNotFoundException The user does not exist in the system
     */
    private @NotNull User getUser(String login) throws NotFoundException {
        User user = userRepository.findByLogin(login);
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user;
    }

}
