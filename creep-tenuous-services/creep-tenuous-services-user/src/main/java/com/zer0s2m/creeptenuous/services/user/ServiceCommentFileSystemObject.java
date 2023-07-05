package com.zer0s2m.creeptenuous.services.user;

import com.zer0s2m.creeptenuous.common.exceptions.NotFoundException;
import com.zer0s2m.creeptenuous.models.common.CommentFileSystemObject;

/**
 * Interface for implementing interaction with comments for file system objects
 */
public interface ServiceCommentFileSystemObject {

    /**
     * Create Comment for File System Object
     * @param comment Comment for file object
     * @param fileSystemObject File object name
     * @param userId user id
     * @return comment
     */
    CommentFileSystemObject create(String comment, String fileSystemObject, Long userId);

    /**
     * Create Comment for File System Object
     * @param comment Comment for file object
     * @param fileSystemObject File object name
     * @param login user login
     * @return comment
     */
    CommentFileSystemObject create(String comment, String fileSystemObject, String login);

    /**
     * Delete comment file system object
     * @param id id comment. Must not be {@literal null}.
     * @param userLogin user login. Must not be {@literal null}.
     * @throws NotFoundException not found comments for filesystem objects
     */
    void delete(Long id, String userLogin) throws NotFoundException;

    /**
     * Edit comment file system object
     * @param comment comment for file object
     * @param id id comment. Must not be {@literal null}.
     * @param userLogin user login. Must not be {@literal null}.
     * @return updated comment
     * @throws NotFoundException not found comments for filesystem objects
     */
    CommentFileSystemObject edit(String comment, Long id, String userLogin) throws NotFoundException;

}
