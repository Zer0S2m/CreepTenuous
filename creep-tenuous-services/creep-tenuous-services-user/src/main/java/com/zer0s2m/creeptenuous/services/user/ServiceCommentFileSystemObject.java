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

    void delete();

    void edit();

}
