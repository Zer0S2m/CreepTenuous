package com.zer0s2m.creeptenuous.redis.events;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Redis Delete Directory Object Handler
 */
@Component
class FileRedisDeleteEventHandler implements ApplicationListener<FileRedisDeleteEvent> {

    /**
     * Handle an application event.
     * @param event the event to respond to
     */
    public void onApplicationEvent(@NotNull FileRedisDeleteEvent event) {
        System.out.println(event.getIdFileRedis());
    }

}
