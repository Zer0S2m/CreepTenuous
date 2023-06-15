package com.zer0s2m.creeptenuous.redis.services.security;

import io.jsonwebtoken.Claims;

/**
 * Basic interface for setting user data via <b>JWT tokens</b>
 */
public interface BaseServiceManagerRightsAccess {

    /**
     * Set access claims (resources), from raw access token
     * @param rawAccessToken <b>JWT</b> raw access token, example (string):
     * <pre>
     * Bearer: token...
     * </pre>
     */
    void setAccessClaims(String rawAccessToken);

    /**
     * Set access claims (resources)
     * @param accessClaims This is ultimately a JSON map and any values can be added to it, but JWT standard
     *                     names are provided as type-safe getters and setters for convenience.
     */
    void setAccessClaims(Claims accessClaims);

    /**
     * Set data from access token
     * @param accessToken <b>JWT</b> access token
     */
    void setAccessToken(String accessToken);

    /**
     * Getting login user
     * @return login user
     */
    String getLoginUser();

    /**
     * Setting login user
     * @param loginUser login user
     */
    void setLoginUser(String loginUser);

}
