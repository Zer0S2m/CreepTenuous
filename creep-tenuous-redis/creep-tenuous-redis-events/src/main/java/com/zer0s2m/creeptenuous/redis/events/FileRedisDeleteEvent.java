package com.zer0s2m.creeptenuous.redis.events;

import org.springframework.context.ApplicationEvent;

/**
 * Event deleting a file object from Redis
 */
class FileRedisDeleteEvent extends ApplicationEvent {

    /**
     * System directory name in Redis
     */
    private String idFileRedis;

    FileRedisDeleteEvent(Object source) {
        super(source);
    }

    public String toString(){
        return this.getClass().getName() + "[source=" + this.source + "] " +
                "Removal event of a radish object from the system";
    }

    /**
     * Get the system name of a file in Redis
     * @return system name of a file in Redis
     */
    public String getIdFileRedis() {
        return idFileRedis;
    }

    /**
     * Set system directory name in Redis
     * @param idFileRedis system directory name in Redis
     */
    public void setIdFileRedis(String idFileRedis) {
        this.idFileRedis = idFileRedis;
    }

}
