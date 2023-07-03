package com.zer0s2m.creeptenuous.integration.implants.api;

import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.integration.implants.services.ServiceIntegration;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Controller responsible for integrating a third-party service for cleaning file storage
 */
@V1APIRestController
@ConditionalOnExpression("${integration.implants.enabled:false}")
public class ControllerApiIntegration {

    private final ServiceIntegration serviceIntegration;

    @Autowired
    public ControllerApiIntegration(ServiceIntegration serviceIntegration) {
        this.serviceIntegration = serviceIntegration;
    }

    /**
     * Call the file storage cleanup method from a third-party service
     */
    @GetMapping("/integration/implants/run")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    @RolesAllowed("ROLE_ADMIN")
    public void run() {
        serviceIntegration.run();
    }

}
