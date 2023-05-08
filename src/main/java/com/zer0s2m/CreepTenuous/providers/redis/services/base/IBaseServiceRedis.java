package com.zer0s2m.CreepTenuous.providers.redis.services.base;

public interface IBaseServiceRedis<T> {
    /**
     * Push in redis one object
     * @param objRedis must not be {@literal null}.
     */
    void push(T objRedis);
}
