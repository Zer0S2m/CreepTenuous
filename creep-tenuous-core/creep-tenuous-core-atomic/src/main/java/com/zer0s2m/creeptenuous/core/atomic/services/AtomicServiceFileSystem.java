package com.zer0s2m.creeptenuous.core.atomic.services;

import com.zer0s2m.creeptenuous.core.atomic.annotations.CoreServiceFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.handlers.AtomicSystemCallManager;

/**
 * Interface for classes that interact with the file system and handle system errors
 * <p>The class must be inherited from the given interface in order to be called
 * through the <b>system manager</b> {@link AtomicSystemCallManager} for atomic mode</p>
 * <p>Use with annotation {@link CoreServiceFileSystem}</p>
 */
public interface AtomicServiceFileSystem {
}
