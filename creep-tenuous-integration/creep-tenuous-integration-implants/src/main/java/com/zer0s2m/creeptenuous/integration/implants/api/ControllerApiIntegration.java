package com.zer0s2m.creeptenuous.integration.implants.api;

import com.zer0s2m.creeptenuous.integration.implants.ControllerApiIntegrationDoc;
import com.zer0s2m.creeptenuous.integration.implants.ResponseServerUnavailableApi;
import com.zer0s2m.creeptenuous.integration.implants.ResponseStatisticsApi;
import com.zer0s2m.creeptenuous.integration.implants.ServiceIntegration;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

import java.util.List;

/**
 * Controller responsible for integrating a third-party service for cleaning file storage
 */
@RestController
@RequestMapping("/api/v1")
@ConditionalOnExpression("${integration.implants.enabled:false}")
public class ControllerApiIntegration implements ControllerApiIntegrationDoc {

    private final ServiceIntegration serviceIntegration;

    @Autowired
    public ControllerApiIntegration(ServiceIntegration serviceIntegration) {
        this.serviceIntegration = serviceIntegration;
    }

    /**
     * Call the file storage cleanup method from a third-party service
     */
    @Override
    @GetMapping("/integration/implants/run")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    @RolesAllowed("ROLE_ADMIN")
    public void run() {
        serviceIntegration.run();
    }

    /**
     * Get statistics on all deleted objects
     * @return objects
     */
    @Override
    @GetMapping("/integration/implants/statistics")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    @RolesAllowed("ROLE_ADMIN")
    public List<ResponseStatisticsApi> getStatistics() {
        return serviceIntegration.getStatistics();
    }

    @ExceptionHandler(RestClientException.class)
    @ResponseStatus(code = HttpStatus.SERVICE_UNAVAILABLE)
    public ResponseServerUnavailableApi handle(RestClientException ignored) {
        return new ResponseServerUnavailableApi(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "The server is not ready to handle the request");
    }

}
