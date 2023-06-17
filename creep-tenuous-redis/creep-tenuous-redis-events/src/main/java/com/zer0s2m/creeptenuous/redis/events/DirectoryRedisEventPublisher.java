package com.zer0s2m.creeptenuous.redis.events;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Class for triggering directory object deletion events from Redis
 */
@Component
public class DirectoryRedisEventPublisher implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(@NotNull ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    /**
     * Trigger the removal event of a file object from Redis
     * @param getNamesFileSystemObject
     * system names of file system objects
     */
    public void publishDelete(List<String> getNamesFileSystemObject) {
        DirectoryRedisDeleteEvent event = new DirectoryRedisDeleteEvent(this);
        event.setNamesFileSystemObject(getNamesFileSystemObject);
        publisher.publishEvent(event);
    }

}
