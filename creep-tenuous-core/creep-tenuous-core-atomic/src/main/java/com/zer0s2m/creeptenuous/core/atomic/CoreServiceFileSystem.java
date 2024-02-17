package com.zer0s2m.creeptenuous.core.atomic;

import java.lang.annotation.*;

/**
 * Basic annotation for a service that works with the file system and system error handling
 * <p>Required to get data to the specified method</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface CoreServiceFileSystem {

    /**
     * The name of the method that should be called in the <b>system manager</b> {@link AtomicSystemCallManager}
     * <p>to handle errors and push them up</p>
     * @return name method
     */
    String method();

}
