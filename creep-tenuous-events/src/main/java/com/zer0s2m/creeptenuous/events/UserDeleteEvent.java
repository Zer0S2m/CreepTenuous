package com.zer0s2m.creeptenuous.events;

import org.springframework.context.ApplicationEvent;

class UserDeleteEvent extends ApplicationEvent {

    private String userLogin;

    public UserDeleteEvent(Object source) {
        super(source);
    }

    public String userLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

}
