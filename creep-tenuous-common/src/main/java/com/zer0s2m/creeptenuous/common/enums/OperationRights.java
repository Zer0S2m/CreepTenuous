package com.zer0s2m.creeptenuous.common.enums;

import java.util.List;

/**
 * Operations for interacting with a file system object
 */
public enum OperationRights {
    MOVE,
    COPY,
    UPLOAD,
    DOWNLOAD,
    CREATE,
    DELETE,
    SHOW,
    ALL;

    /**
     * Getting basic operations. {@link OperationRights#ALL} not included
     * @return basic operations
     */
    static public List<OperationRights> baseOperations() {
        return List.of(MOVE, COPY, UPLOAD, DOWNLOAD, CREATE, DELETE, SHOW);
    }
}
