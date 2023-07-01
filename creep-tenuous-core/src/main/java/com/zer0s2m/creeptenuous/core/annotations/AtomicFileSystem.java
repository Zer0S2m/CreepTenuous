package com.zer0s2m.creeptenuous.core.annotations;

import java.lang.annotation.*;

/**
 * <b>Annotation</b> for error handling via method call via system manager
 * {@link com.zer0s2m.creeptenuous.core.handlers.AtomicSystemCallManager}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface AtomicFileSystem {

    /**
     * Custom name of the operation in which the atomic mode will be enabled
     * @return custom name
     */
    String name() default "";

    /**
     * Error handlers called by method via system manager call
     * {@link com.zer0s2m.creeptenuous.core.handlers.AtomicSystemCallManager}
     * @return handlers exception {@link AtomicFileSystemExceptionHandler#exception()}
     */
    AtomicFileSystemExceptionHandler[] handlers();

}
