package com.zer0s2m.creeptenuous.core.services.impl;

import com.zer0s2m.creeptenuous.core.services.ServiceFileSystemExceptionHandler;

/**
 * Basic interface for services that interact with the file system
 */
public class ServiceFileSystemExceptionHandlerImpl implements ServiceFileSystemExceptionHandler {
    /**
     * Handle exception caused by file system
     *
     * @param t exception
     */
    @Override
    public void handleException(Throwable t) {
        System.out.println(t.getClass());
    }
}
