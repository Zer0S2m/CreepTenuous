package com.zer0s2m.creeptenuous.integration.implants.services.impl;

import com.zer0s2m.creeptenuous.integration.core.ServiceIntegrationJwt;
import com.zer0s2m.creeptenuous.integration.implants.services.ServiceIntegration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Service for a layer of calling third-party methods from an internal ecosystem service
 */
@Service("service-integration-implants")
public class ServiceIntegrationImpl implements ServiceIntegration {

    private final RSAKeys rsaKeys = new RSAKeys();

    private final RestOperations restTemplate = new RestTemplate();

    private final ServiceIntegrationJwt serviceIntegrationJwt = new ServiceIntegrationJwtImpl();

    @Value("${integration.implants.host:localhost}")
    private String host;

    @Value("${integration.implants.port:9191}")
    private String port;

    public ServiceIntegrationImpl() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    }

    /**
     * Starting File Storage Cleanup
     */
    @Override
    public void run() {
        final String url = url();
        final JSONObject dataIntegrationApi = new JSONObject();
        final HttpHeaders headers = new HttpHeaders();

        dataIntegrationApi.put("token", serviceIntegrationJwt.generateToken(rsaKeys.getPrivateKey()));
        headers.setContentType(MediaType.APPLICATION_JSON);

        final HttpEntity<String> entity = new HttpEntity<>(dataIntegrationApi.toString(), headers);

        restTemplate.postForEntity(url, entity, Void.class);
    }

    @Contract(pure = true)
    private @NotNull String url() {
        return "http://" + host + ":" + port + "/api/v1/integration/run-clean";
    }

}
