package com.zer0s2m.creeptenuous.common.query;

import java.util.List;
import java.util.UUID;

/**
 * Projection to get file objects with their set user categories
 */
public interface IMapFileObjectToCategory {

    /**
     * Get user ID entity
     * @return ID
     */
    Integer getUserCategoryId();

    /**
     * Get system names of file objects
     * @return system names of file objects
     */
    List<UUID> getFileSystemObjects();

}
