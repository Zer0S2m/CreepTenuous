package com.zer0s2m.creeptenuous.common.query;

import java.util.List;
import java.util.UUID;

/**
 * Projection to get file objects that are in exclusions and aggregate by users
 */
public interface IFileObjectsExclusions {

    /**
     * Get user login
     * @return user login
     */
    String getUserLogin();

    /**
     * Get system names of file objects
     * @return system names of file objects
     */
    List<UUID> getFileSystemObjects();

}
