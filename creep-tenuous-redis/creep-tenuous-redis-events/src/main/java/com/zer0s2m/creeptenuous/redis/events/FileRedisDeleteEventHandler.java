package com.zer0s2m.creeptenuous.redis.events;

import com.zer0s2m.creeptenuous.common.enums.ManagerRights;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Redis Delete Directory Object Handler
 */
@Component
class FileRedisDeleteEventHandler implements ApplicationListener<FileRedisDeleteEvent> {

    static private final String SEPARATOR_UNIQUE_KEY = ManagerRights.SEPARATOR_UNIQUE_KEY.get();

    /**
     * Handle an application event.
     * @param event the event to respond to
     */
    public void onApplicationEvent(@NotNull FileRedisDeleteEvent event) {
        System.out.println(event.getIdFileRedis());
    }

}
