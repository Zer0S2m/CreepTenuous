package com.zer0s2m.creeptenuous.services.user;

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
     */
    void delete(Long id);

    /**
     * Edit comment file system object
     * @param comment comment for file object
     * @param id id comment. Must not be {@literal null}.
     */
    CommentFileSystemObject edit(String comment, Long id);

}
