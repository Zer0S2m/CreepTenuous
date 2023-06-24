package com.zer0s2m.creeptenuous.events;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserEventPublisher implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(@NotNull ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    /**
     * Trigger the removal event of a user
     */
    public void publishDelete() {
        UserDeleteEvent event = new UserDeleteEvent(this);
        publisher.publishEvent(event);
    }

}
