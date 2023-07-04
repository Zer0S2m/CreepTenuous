package com.zer0s2m.creeptenuous.integration.core;

import java.security.PrivateKey;

/**
 * Interface for implementing the creation of jwt tokens for integration with
 * services from the ecosystem
 */
public interface ServiceIntegrationJwt {

    /**
     * Generate a token to authorize a third-party service using a private key
     * @param privateKey private key
     * @return access JWT token
     */
    String generateToken(PrivateKey privateKey);

}
