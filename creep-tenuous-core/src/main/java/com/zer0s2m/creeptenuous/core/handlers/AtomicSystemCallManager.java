package com.zer0s2m.creeptenuous.core.handlers;

import com.zer0s2m.creeptenuous.core.annotations.CoreServiceFileSystem;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * System manager for callable objects that work with the file system
 * <p>It is better to call all called objects through the manager - it is necessary to roll back
 *the loaded data when system exceptions are throws {@link java.io.IOException}</p>
 */
public final class AtomicSystemCallManager {

    /**
     * Call a method through the system manager to handle <b>exceptions</b> and push them up.
     * <p>The name of the method is given in: {@link CoreServiceFileSystem#method}</p>
     * @param instance base atomic service file system {@link AtomicServiceFileSystem}
     * @param args arguments in method
     * @return data {@link T}
     * @param <T> return type
     * @throws NoSuchMethodException not found method
     * @throws InvocationTargetException invocation target exception
     * @throws IllegalAccessException An IllegalAccessException is thrown when an application
     *                                tries to reflectively create an instance
     */
    public static <T> T call(AtomicServiceFileSystem instance, Object... args)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?>[] argTypes = new Class<?>[args.length];

        for(int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].getClass();
        }

        boolean isServiceCoreFileSystem = false;
        String method = null;
        Annotation [] annotationsClass = instance.getClass().getAnnotations();
        for (Annotation as : annotationsClass) {
            if (as.annotationType().equals(CoreServiceFileSystem.class)) {
                isServiceCoreFileSystem = true;
                method = ((CoreServiceFileSystem) as).method();
            }
        }
        if (!isServiceCoreFileSystem) {
            throw new RuntimeException("The class has no type annotation: ["
                    + CoreServiceFileSystem.class.getCanonicalName() + "]");
        }

        Method targetMethod = instance
                .getClass()
                .getDeclaredMethod(method, argTypes);

        return (T) targetMethod.invoke(instance, args);
    }
}
