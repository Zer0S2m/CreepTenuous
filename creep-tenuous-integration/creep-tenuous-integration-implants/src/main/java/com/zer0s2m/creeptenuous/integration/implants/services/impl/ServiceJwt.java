package com.zer0s2m.creeptenuous.integration.implants.services.impl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for authenticating jwt tokens
 */
class ServiceJwt {

    /**
     * Generate a token to authorize a third-party service using a private key
     * @param privateKey private key
     * @return access JWT token
     */
    public static String generateToken(PrivateKey privateKey) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("integration", "main");

        return Jwts
                .builder()
                .setClaims(claims)
                .signWith(privateKey, SignatureAlgorithm.RS512)
                .compact();
    }

}
