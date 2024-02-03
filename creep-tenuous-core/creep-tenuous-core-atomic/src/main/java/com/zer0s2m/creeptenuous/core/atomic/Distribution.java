package com.zer0s2m.creeptenuous.core.atomic;

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
