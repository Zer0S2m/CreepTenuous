package com.zer0s2m.creeptenuous.api.controllers.user;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiShortcutFileSystemObjectsUserDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.services.user.ServiceShortcutFileSystemObjectsUser;
import org.springframework.beans.factory.annotation.Autowired;

@V1APIRestController
public class ControllerApiShortcutFileSystemObjectsUser implements ControllerApiShortcutFileSystemObjectsUserDoc {

    private final ServiceShortcutFileSystemObjectsUser serviceShortcutFileSystemObjectsUser;

    @Autowired
    public ControllerApiShortcutFileSystemObjectsUser(
            ServiceShortcutFileSystemObjectsUser serviceShortcutFileSystemObjectsUser) {
        this.serviceShortcutFileSystemObjectsUser = serviceShortcutFileSystemObjectsUser;
    }

    @Override
    public void create() {

    }

    @Override
    public void delete() {

    }

}
