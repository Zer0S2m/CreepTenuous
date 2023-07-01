package com.zer0s2m.creeptenuous.redis.services.user;

/**
 * Interface for implementing blocking and unblocking a user for a period of time
 */
public interface ServiceBlockUserRedis {

    /**
     * Block a user for a period of time
     * @param expiration block expiration
     * @param login user login
     */
    default void block(Long expiration, String login) {
        block(expiration, login, false, null);
    }

    /**
     * Block a user for a period of time
     * @param expiration block expiration
     * @param login user login
     * @param isDelayed Whether the lock is delayed
     * @param delayed block expiration delayed
     */
     void block(Long expiration, String login, boolean isDelayed, Long delayed);

    /**
     * Unblock user
     * @param login user login
     */
    void unblock(String login);

    /**
     * Check if account is blocked
     * @param login user login
     * @return is non account blocked
     */
    boolean check(String login);

}
