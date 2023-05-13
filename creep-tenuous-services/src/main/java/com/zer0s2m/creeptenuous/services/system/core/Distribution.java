package com.zer0s2m.creeptenuous.services.system.core;

import java.util.UUID;

public final class Distribution {
    /**
     * Get random name file
     * @return <b>uuid</b> string
     */
    public static String getUUID() {
        return UUID.randomUUID().toString();
    }
}
