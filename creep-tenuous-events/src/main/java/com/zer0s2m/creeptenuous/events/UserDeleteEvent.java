package com.zer0s2m.creeptenuous.events;

import org.springframework.context.ApplicationEvent;

class UserDeleteEvent extends ApplicationEvent {

    public UserDeleteEvent(Object source) {
        super(source);
    }

}
