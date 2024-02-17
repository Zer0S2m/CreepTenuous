package com.zer0s2m.creeptenuous.core.atomic;

import java.lang.annotation.*;

/**
 * A set of rules for error handling, works in conjunction with {@link AtomicFileSystem}
 * and system call manager for atomic mode {@link AtomicSystemCallManager}
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
     * The target exception to be handled and move up the stack
     * @return target exception
     */
    Class<? extends Throwable> exception() default Exception.class;

    /**
     * The target exception to the specified type (class, subclass, or interface) to handle and move up the stack
     * @return target exception
     */
    Class<? extends Throwable> exceptionMulti() default Exception.class;

    /**
     * Whether to use a handler on the target exception for the specified type (class, subclass, or interface)
     * to handle and move up the stack.
     * @return whether to use a handler on the target exception
     */
    boolean isExceptionMulti() default false;

    /**
     * Type of operation when handling an exception, necessary for
     * {@link AtomicSystemCallManager}
     * @return type operation
     */
    ContextAtomicFileSystem.Operations operation();

}
