package com.zer0s2m.creeptenuous.core.annotations;

import com.zer0s2m.creeptenuous.core.handlers.AtomicSystemCallManager;

import java.lang.annotation.*;

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
