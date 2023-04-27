package com.zer0s2m.CreepTenuous.providers.redis.services.base;

public interface IBaseServiceRedis<T> {
    void push(T objRedis);
}
