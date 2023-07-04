package com.zer0s2m.creeptenuous.api.controllers.user;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiCommentFileSystemObjectUserDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.services.user.ServiceCommentFileSystemObject;
import org.springframework.beans.factory.annotation.Autowired;

@V1APIRestController
public class ControllerApiCommentFileSystemObjectUser implements ControllerApiCommentFileSystemObjectUserDoc {

    private final ServiceCommentFileSystemObject serviceCommentFileSystemObject;

    @Autowired
    public ControllerApiCommentFileSystemObjectUser(
            ServiceCommentFileSystemObject serviceCommentFileSystemObject) {
        this.serviceCommentFileSystemObject = serviceCommentFileSystemObject;
    }

    @Override
    public void create() {

    }

    @Override
    public void delete() {

    }

    @Override
    public void edit() {

    }

}
