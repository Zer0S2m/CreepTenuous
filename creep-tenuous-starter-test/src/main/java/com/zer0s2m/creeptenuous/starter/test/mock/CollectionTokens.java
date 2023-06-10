package com.zer0s2m.creeptenuous.starter.test.mock;

/**
 * Collection for storing JWT tokens
 * @param accessToken access JWT token
 * @param refreshToken refresh JWT token
 * @param <T> type instance (recommended {@link String})
 * @param <E> type instance (recommended {@link String})
 */
public record CollectionTokens<T, E>(
        T accessToken,
        E refreshToken
) { }
