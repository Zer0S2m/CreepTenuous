package com.zer0s2m.creeptenuous.events;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
class UserDeleteEventHandler implements ApplicationListener<UserDeleteEvent> {

    /**
     * Handle an application event.
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(@NotNull UserDeleteEvent event) {
        System.out.println(event);
    }

}
