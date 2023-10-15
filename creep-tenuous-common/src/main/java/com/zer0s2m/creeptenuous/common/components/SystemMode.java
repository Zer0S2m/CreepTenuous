package com.zer0s2m.creeptenuous.common.components;

import java.util.Objects;

final public class SystemMode {

    static private final String CT_MODE = System.getenv("CT_MODE");

    static private final String CT_MODE_FILE_BALANCER = System.getenv("CT_MODE_FILE_BALANCER");

    /**
     * Give permission to split a file when uploading it in development mode.
     * @return Permission.
     */
    public static boolean isSplitFileInUpload() {
        return Objects.equals(CT_MODE, "dev") && Objects.equals(CT_MODE_FILE_BALANCER, "split");
    }

}
