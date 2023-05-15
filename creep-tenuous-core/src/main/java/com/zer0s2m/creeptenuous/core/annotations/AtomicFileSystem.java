package com.zer0s2m.creeptenuous.core.annotations;

import com.zer0s2m.creeptenuous.core.handlers.ServiceFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.handlers.impl.ServiceFileSystemExceptionHandlerImpl;

import java.lang.annotation.*;

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
     * The class that handles the exception and pushes it up
     * @return class handler
     */
    Class<? extends ServiceFileSystemExceptionHandler> handler() default ServiceFileSystemExceptionHandlerImpl.class;

    /**
     * The target exception to be handled and raised up
     * @return target exception
     */
    Class<? extends Throwable> exception() default Exception.class;
}
