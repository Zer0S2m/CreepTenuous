package com.zer0s2m.creeptenuous.redis.services.security;

import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.redis.exceptions.NoRightsRedisException;

import java.io.IOException;

/**
 * Extended service for managing user rights to interact with the target file system object.
 * <p>Designed for heavier objects in the form of <b>catalogs</b></p>
 */
public interface ServiceManagerRightsExtended {

    /**
     * Checking permissions to perform some actions on a certain file object - operation {@link OperationRights#DELETE}
     * @param fileSystemObject file system object
     * @throws IOException signals that an I/O exception of some sort has occurred
     * @throws NoRightsRedisException Insufficient rights to perform the operation
     */
    void checkRightByOperationDeleteDirectory(String fileSystemObject) throws IOException, NoRightsRedisException;

    /**
     * Checking permissions to perform some actions on a certain file object - operation {@link OperationRights#MOVE}
     * @param fileSystemObject file system object
     * @throws IOException signals that an I/O exception of some sort has occurred
     * @throws NoRightsRedisException Insufficient rights to perform the operation
     */
    void checkRightByOperationMoveDirectory(String fileSystemObject) throws IOException, NoRightsRedisException;

    /**
     * Set the parameter responsible for the type of file system object, file or directory
     * @param isDirectory is directory
     */
    void setIsDirectory(boolean isDirectory);

    /**
     * Get the parameter responsible for the file system object type, file or directory
     * @return is directory
     */
    boolean getIsDirectory();

}
