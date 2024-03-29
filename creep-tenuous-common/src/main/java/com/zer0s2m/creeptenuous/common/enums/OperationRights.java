package com.zer0s2m.creeptenuous.common.enums;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/**
 * Operations for interacting with a file system object
 */
public enum OperationRights {

    /**
     * Moving file system objects from their own or to those that have access
     */
    MOVE,

    /**
     * Deleting file system objects from their own or to those that have access
     */
    COPY,

    /**
     * Downloading File System Objects to Directories
     */
    UPLOAD,

    /**
     * Downloading file system objects
     */
    DOWNLOAD,

    /**
     * Creating new file system objects in directories
     */
    CREATE,

    /**
     * Deleting a File System Object.
     * <p>If the object is a directory, then it will be deleted if the user has <b>all</b> the necessary
     * rights in the subdirectories</p>
     */
    DELETE,

    /**
     * View file system object
     */
    SHOW,

    /**
     * Renaming file objects
     */
    RENAME,

    /**
     * Analyzing a file object - viewing granted rights, etc.
     */
    ANALYSIS,

    /**
     * Rights to all basic operations
     */
    ALL;

    /**
     * Getting basic operations. {@link OperationRights#ALL} not included
     * @return basic operations
     */
    @Contract(pure = true)
    static public @Unmodifiable List<OperationRights> baseOperations() {
        return List.of(MOVE, COPY, UPLOAD, DOWNLOAD, CREATE, DELETE, SHOW, RENAME, ANALYSIS);
    }

}
