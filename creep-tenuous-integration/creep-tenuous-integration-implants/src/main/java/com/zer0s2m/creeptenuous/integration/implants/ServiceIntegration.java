package com.zer0s2m.creeptenuous.integration.implants;

import java.util.List;

/**
 * Interface for implementing a layer of calling third-party methods from an internal ecosystem service
 */
public interface ServiceIntegration {

    /**
     * Starting File Storage Cleanup
     */
    void run();

    /**
     * Get statistics on all deleted objects
     * @return objects
     */
    List<ResponseStatisticsApi> getStatistics();

}
