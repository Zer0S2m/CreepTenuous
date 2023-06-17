package com.zer0s2m.creeptenuous.redis.events;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

/**
 * Class for triggering file object deletion events from Redis
 */
@Component
public class FileRedisEventPublisher implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher (@NotNull ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    /**
     * Trigger the removal event of a file object from Redis
     * @param idFileRedis system name of a file in Redis
     */
    public void publishDelete(String idFileRedis) {
        FileRedisDeleteEvent event = new FileRedisDeleteEvent(this);
        event.setIdFileRedis(idFileRedis);
        publisher.publishEvent(event);
    }

}
