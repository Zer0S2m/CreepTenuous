package com.zer0s2m.creeptenuous.core.handlers;

import com.zer0s2m.creeptenuous.core.annotations.AtomicFileSystem;
import com.zer0s2m.creeptenuous.core.annotations.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.annotations.CoreServiceFileSystem;
import com.zer0s2m.creeptenuous.core.context.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.services.AtomicServiceFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * System manager for callable objects that work with the file system
 * <p>It is better to call all called objects through the manager - it is necessary to roll back
 * the loaded data when system exceptions are throws {@link java.io.IOException}</p>
 * Example service:
 * <pre>{@code
 * @CoreServiceFileSystem(method = "method")
 * public class ServiceFileSystem {
 *
 *      @AtomicFileSystem(
 *          name = "method-service",
 *          handlers = {
 *              @AtomicFileSystemExceptionHandler(
 *                  handler = ServiceFileSystemExceptionHandlerOperationCreate.class,
 *                  exception = IOException.class,
 *                  operation = ContextAtomicFileSystem.Operations.CREATE
 *              )
 *          }
 *     )
 *     public Path method(Path dir) {
 *         return Files.createDirectory(dir);
 *     }
 * }
 * }</pre>
 */
public final class AtomicSystemCallManager {

     private static final Logger logger = LoggerFactory.getLogger(AtomicSystemCallManager.class);

    /**
     * Context for working with the file system in <b>atomic mode</b>
     */
    private static final ContextAtomicFileSystem contextAtomicFileSystem = ContextAtomicFileSystem.getInstance();

    /**
     * Call a method through the system manager to handle <b>exceptions</b> and push them up.
     * <p>The name of the method is given in: {@link CoreServiceFileSystem#method}</p>
     * <p>An atomic mode context is <b><u>required</u></b> to invoke exception handling
     * {@link com.zer0s2m.creeptenuous.core.context.ContextAtomicFileSystem}</p>
     * <p>Subsequent call to the specified operation handler in
     * {@link AtomicFileSystemExceptionHandler#operation()}</p>
     * <p>If the method takes one of the arguments that the class has {@link ArrayList},
     * it will be converted to {@link List}. Call through the system manager, in methods it
     * is better to use an array with the type {@link List}</p>
     * @param instance base atomic service file system
     *                 {@link AtomicServiceFileSystem} and {@link CoreServiceFileSystem}
     * @param args arguments in method
     * @return data {@link T}
     * @param <T> return type
     * @throws NoSuchMethodException not found method
     * @throws InvocationTargetException called if an exception was thrown in the method
     * @throws IllegalAccessException an IllegalAccessException is thrown when an application
     *                                tries to reflectively create an instance
     */
    public static <T> T call(AtomicServiceFileSystem instance, Object... args)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?>[] argTypes = new Class<?>[args.length];

        for (int i = 0; i < args.length; i++) {
            if (args[i].getClass().equals(ArrayList.class)) {
                argTypes[i] = List.class;
                continue;
            }
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

        List<AtomicFileSystemExceptionHandler> systemExceptionHandlers;
        AtomicFileSystem atomicFileSystem = targetMethod.getAnnotation(AtomicFileSystem.class);
        if (atomicFileSystem != null) {
            systemExceptionHandlers = Arrays.asList(atomicFileSystem.handlers());
        } else {
            systemExceptionHandlers = null;
        }

        List<ContextAtomicFileSystem.Operations> operations = new ArrayList<>();

        if (systemExceptionHandlers != null) {
            String nameMethod;
            if (Objects.equals(atomicFileSystem.name(), "")) {
                nameMethod = targetMethod.getName();
            } else {
                nameMethod = atomicFileSystem.name();
            }
            try {
                logger.info(String.format(
                        "Call method [%s] in service [%s] from atomic system manager",
                        nameMethod, targetMethod.getDeclaringClass().getName()
                ));
                return (T) targetMethod.invoke(instance, args);
            } catch (Throwable e) {
                for (AtomicFileSystemExceptionHandler atomicFileSystemExceptionHandler: systemExceptionHandlers) {
                    if (e.getCause().getClass().equals(atomicFileSystemExceptionHandler.exception())) {
                        ServiceFileSystemExceptionHandler handler = atomicFileSystemExceptionHandler
                                .handler()
                                .getDeclaredConstructor()
                                .newInstance();
                        handler.handleException(
                                e.getCause(),
                                contextAtomicFileSystem.getOperationsData()
                        );
                        ContextAtomicFileSystem.Operations operation = atomicFileSystemExceptionHandler.operation();
                        if (!operations.contains(operation)) {
                            operations.add(operation);
                        }
                    }
                }
                throw e;
            }
        }

        operations.forEach(contextAtomicFileSystem::clearOperationsData);

        return (T) targetMethod.invoke(instance, args);
    }
}
