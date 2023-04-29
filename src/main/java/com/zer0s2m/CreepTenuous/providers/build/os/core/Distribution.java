package com.zer0s2m.CreepTenuous.providers.build.os.core;

import java.util.UUID;

public class Distribution {
    /**
     * Get random name file
     * @return uuid string
     */
    public static String getUUID() {
        return UUID.randomUUID().toString();
    }
}
