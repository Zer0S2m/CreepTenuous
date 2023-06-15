package com.zer0s2m.creeptenuous.redis.services.system.base;

/**
 * Basic service for populating filesystem objects in Redis
 * @param <T> Filesystem object in Redis view
 */
public interface BaseServiceRedis<T> {

    /**
     * Push in redis one object
     * @param objRedis must not be {@literal null}.
     */
    void push(T objRedis);

}
