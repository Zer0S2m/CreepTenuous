package com.zer0s2m.creeptenuous.redis.services.system.base;

public interface BaseServiceRedis<T> {
    /**
     * Push in redis one object
     * @param objRedis must not be {@literal null}.
     */
    void push(T objRedis);
}
