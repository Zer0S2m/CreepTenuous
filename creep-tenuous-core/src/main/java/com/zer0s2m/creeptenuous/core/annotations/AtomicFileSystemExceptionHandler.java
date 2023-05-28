package com.zer0s2m.creeptenuous.core.annotations;

import com.zer0s2m.creeptenuous.core.context.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.handlers.ServiceFileSystemExceptionHandler;

import java.lang.annotation.*;

/**
 * A set of rules for error handling, works in conjunction with {@link AtomicFileSystem}
 * and system call manager for atomic mode {@link com.zer0s2m.creeptenuous.core.handlers.AtomicSystemCallManager}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface AtomicFileSystemExceptionHandler {
    /**
     * The class that handles the exception and pushes it up {@link AtomicFileSystemExceptionHandler#exception()}
     * @return class handler
     */
    Class<? extends ServiceFileSystemExceptionHandler> handler();

    /**
     * The target exception to be handled and raised up
     * @return target exception
     */
    Class<? extends Throwable> exception() default Exception.class;

    /**
     * Type of operation when handling an exception, necessary for
     * {@link com.zer0s2m.creeptenuous.core.handlers.AtomicSystemCallManager}
     * @return type operation
     */
    ContextAtomicFileSystem.Operations operation();
}
