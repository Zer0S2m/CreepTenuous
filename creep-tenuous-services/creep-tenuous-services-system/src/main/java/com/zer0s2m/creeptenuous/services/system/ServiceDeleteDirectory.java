package com.zer0s2m.creeptenuous.services.system;

import java.io.IOException;
import java.util.List;

/**
 * Maintenance service directory removal
 */
public interface ServiceDeleteDirectory {

    /**
     * Delete directory from system
     * @param systemParents parts of the system path - source
     * @param systemName system name directory
     * @throws IOException system error
     */
    void delete(List<String> systemParents, String systemName) throws IOException;

}
