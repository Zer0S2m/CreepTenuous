package com.zer0s2m.creeptenuous.core.atomic;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

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
 *         return FilesContextAtomic.createDirectory(dir);
 *     }
 * }
 * }</pre>
 */
public final class AtomicSystemCallManager {

    /** The CGLIB class separator: {@code "$$"}. */
    public static final String CGLIB_CLASS_SEPARATOR = "$$";

    private static final Logger logger = LoggerFactory.getLogger(AtomicSystemCallManager.class);

    /**
     * Context for working with the file system in <b>atomic mode</b>
     */
    private static final ContextAtomicFileSystem contextAtomicFileSystem = ContextAtomicFileSystem.getInstance();

    /**
     * Call a method through the system manager to handle <b>exceptions</b> and push them up.
     * <p>The name of the method is given in: {@link CoreServiceFileSystem#method}</p>
     * <p>An atomic mode context is <b><u>required</u></b> to invoke exception handling
     * {@link ContextAtomicFileSystem}</p>
     * <p>Subsequent call to the specified operation handler in
     * {@link AtomicFileSystemExceptionHandler#operation()}</p>
     * <p>If the method takes one of the arguments that the class has {@link ArrayList},
     * it will be converted to {@link List}. Call through the system manager, in methods it
     * is better to use an array with the type {@link List}</p>
     * <p>Captures classes if they are proxies. See the release note:
     * <a href="https://docs.spring.io/spring-framework/docs/3.2.0.RELEASE/spring-framework-reference/html/migration-3.2.html">Spring Framework 3.2</a></p>
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
    @SuppressWarnings("unchecked")
    public static <T> T call(AtomicServiceFileSystem instance, Object @NotNull ... args)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?>[] argTypes = new Class<?>[args.length];

        for (int i = 0; i < args.length; i++) {
            if (args[i].getClass().equals(ArrayList.class)) {
                argTypes[i] = List.class;
                continue;
            }
            if (checkIsUnixPath(args[i].getClass().getCanonicalName())) {
                argTypes[i] = Path.class;
                continue;
            }

            argTypes[i] = args[i].getClass();
        }

        AtomicBoolean isServiceCoreFileSystem = new AtomicBoolean(false);
        String method = null;

        Class<?> instanceFromProxy = getUserClass(instance.getClass());
        Annotation [] annotationsClass = instanceFromProxy.getAnnotations();

        for (Annotation as : annotationsClass) {
            if (as.annotationType().equals(CoreServiceFileSystem.class)) {
                isServiceCoreFileSystem.set(true);
                method = ((CoreServiceFileSystem) as).method();
            }
        }
        if (!isServiceCoreFileSystem.get()) {
            throw new RuntimeException("The class has no type annotation: ["
                    + CoreServiceFileSystem.class.getCanonicalName() + "]");
        }

        assert method != null;
        Method targetMethod = instanceFromProxy.getDeclaredMethod(method, argTypes);

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

            String className = targetMethod.getDeclaringClass().getName();

            try {
                logger.info(String.format(
                        "Call method [%s] in service [%s] from atomic system manager",
                        nameMethod, className
                ));

                T result = (T) targetMethod.invoke(instance, args);

                deleteAtomicFileObjects();

                contextAtomicFileSystem.clearOperationsData();

                return result;
            } catch (Throwable e) {
                for (AtomicFileSystemExceptionHandler atomicFileSystemExceptionHandler: systemExceptionHandlers) {
                    Class<? extends Throwable> classExceptionMulti = atomicFileSystemExceptionHandler
                            .exceptionMulti();
                    if ((e.getCause().getClass().equals(atomicFileSystemExceptionHandler.exception())) ||
                            (atomicFileSystemExceptionHandler.isExceptionMulti() &&
                                    classExceptionMulti.isInstance(e))) {
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

                operations.forEach(contextAtomicFileSystem::clearOperationsData);

                throw e;
            }
        }

        return (T) targetMethod.invoke(instance, args);
    }

    /**
     * Whether the class is a concrete implementation in the runtime (<b>Unix</b>) {@link Path}
     * @param class_ raw class string. inherited from {@link Path}
     * @return is the class
     */
    @Contract(pure = true)
    private static boolean checkIsUnixPath(@NotNull String class_) {
        return class_.equals("sun.nio.fs.UnixPath");
    }

    /**
     * Return the user-defined class for the given class: usually simply the given
     * class, but the original class in case of a CGLIB-generated subclass.
     * @param clazz the class to check
     * @return the user-defined class
     * @see #CGLIB_CLASS_SEPARATOR
     */
    private static @NotNull Class<?> getUserClass(@NotNull Class<?> clazz) {
        if (clazz.getName().contains(CGLIB_CLASS_SEPARATOR)) {
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null && superclass != Object.class) {
                return superclass;
            }
        }
        return clazz;
    }

    private static void deleteAtomicFileObjects() {
        contextAtomicFileSystem.getOperationsData().forEach((key, operationData) -> {
            if (ContextAtomicFileSystem.Operations.DELETE.equals(operationData.get("operation"))) {
                Object path = operationData.get("targetPath");
                if (path != null) {
                    try {
                        Files.deleteIfExists((Path) path);
                    } catch (Throwable ignored) {}
                }
            }
        });
    }

}
