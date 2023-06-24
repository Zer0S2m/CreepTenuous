package com.zer0s2m.creeptenuous.events;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

/**
 * Trigger event - user deletion
 */
@Component
public class UserEventPublisher implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(@NotNull ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    /**
     * Trigger the removal event of a user
     * @param loginUser user login
     */
    public void publishDelete(String loginUser) {
        UserDeleteEvent event = new UserDeleteEvent(this);
        event.setUserLogin(loginUser);
        publisher.publishEvent(event);
    }

}
