package com.zer0s2m.creeptenuous.redis.services.security;

import com.zer0s2m.creeptenuous.common.enums.ManagerRights;
import org.jetbrains.annotations.NotNull;

public interface CollectUniqueKeyRightUserFileObject {

    /**
     * Separator for creating a unique key (from the system name of the file system object and user login)
     */
    String SEPARATOR_UNIQUE_KEY = ManagerRights.SEPARATOR_UNIQUE_KEY.get();

    /**
     * Unpack a unique name using a delimiter {@link CollectUniqueKeyRightUserFileObject#SEPARATOR_UNIQUE_KEY}
     * @param uniqueName a unique name that was created using a delimiter
     * @return the system name of the file system object
     */
    default String unpackingUniqueKey(@NotNull String uniqueName) {
        return uniqueName.split(SEPARATOR_UNIQUE_KEY)[0];
    }

    /**
     * Creating a unique key from the system name of the file system object and the user login
     * @param systemName the system name of the file system object
     * @param loginUser user login
     * @return generated unique key
     */
    default String buildUniqueKey(String systemName, String loginUser) {
        return systemName + SEPARATOR_UNIQUE_KEY + loginUser;
    }

}
