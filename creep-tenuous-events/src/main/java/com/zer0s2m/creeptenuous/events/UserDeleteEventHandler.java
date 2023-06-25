package com.zer0s2m.creeptenuous.events;

import com.zer0s2m.creeptenuous.redis.services.security.ServiceControlUserRights;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Performs the following actions when deleting a user:
 * <ul>
 *     <li>Removing all file system objects from storage</li>
 *     <li>Removing objects from Redis</li>
 *     <li>Remove all assigned and granted user rights</li>
 * </ul>
 */
@Component
class UserDeleteEventHandler implements ApplicationListener<UserDeleteEvent> {

    private final ServiceControlUserRights serviceControlUserRights;

    @Autowired
    public UserDeleteEventHandler(ServiceControlUserRights serviceControlUserRights) {
        this.serviceControlUserRights = serviceControlUserRights;
    }

    /**
     * Handle an application event.
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(@NotNull UserDeleteEvent event) {
        final String userLogin = event.userLogin();
        serviceControlUserRights.removeAssignedPermissionsForUser(userLogin);
        serviceControlUserRights.removeGrantedPermissionsForUser(userLogin);
        serviceControlUserRights.removeFileSystemObjects(userLogin);
        serviceControlUserRights.removeJwtTokensFotUser(userLogin);
    }

}
