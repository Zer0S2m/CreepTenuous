package com.zer0s2m.creeptenuous.api.controllers.user;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiCreateUserDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataCreateDirectory;
import com.zer0s2m.creeptenuous.common.enums.BaseCatalog;
import com.zer0s2m.creeptenuous.common.exceptions.UserAlreadyExistException;
import com.zer0s2m.creeptenuous.common.exceptions.messages.UserAlreadyExistMsg;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceCreateDirectoryRedis;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceCreateDirectoryImpl;
import com.zer0s2m.creeptenuous.services.user.ServiceCreateUser;
import jakarta.annotation.security.RolesAllowed;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.ArrayList;

@V1APIRestController
public class ControllerApiCreateUser implements ControllerApiCreateUserDoc {

    private final ServiceCreateUser createUser;

    private final ServiceCreateDirectoryImpl serviceCreateDirectory;

    private final ServiceCreateDirectoryRedis serviceCreateDirectoryRedis;

    @Autowired
    public ControllerApiCreateUser(ServiceCreateUser createUser,
                                   ServiceCreateDirectoryImpl serviceCreateDirectory,
                                   ServiceCreateDirectoryRedis serviceCreateDirectoryRedis) {
        this.createUser = createUser;
        this.serviceCreateDirectory = serviceCreateDirectory;
        this.serviceCreateDirectoryRedis = serviceCreateDirectoryRedis;
    }

    /**
     * Creating a user in the system
     * @param user Data for creating a user in the system
     * @throws UserAlreadyExistException user already exists
     * @throws IOException if an I/O error occurs or the parent directory does not exist
     */
    @Override
    @PostMapping("/user/create")
    @RolesAllowed("ROLE_ADMIN")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void create(final @RequestBody User user) throws UserAlreadyExistException, IOException {
        createUser.create(user);

        ContainerDataCreateDirectory dataCreateDirectoryDocument = serviceCreateDirectory.create(
                new ArrayList<>(), BaseCatalog.DOCUMENTS.get());
        ContainerDataCreateDirectory dataCreateDirectoryVideo = serviceCreateDirectory.create(
                new ArrayList<>(), BaseCatalog.VIDEO.get());
        ContainerDataCreateDirectory dataCreateDirectoryMusic = serviceCreateDirectory.create(
                new ArrayList<>(), BaseCatalog.MUSIC.get());
        ContainerDataCreateDirectory dataCreateDirectoryImages = serviceCreateDirectory.create(
                new ArrayList<>(), BaseCatalog.IMAGES.get());

        serviceCreateDirectoryRedis.create(dataCreateDirectoryDocument, user.getLogin(), user.getRole().getAuthority());
        serviceCreateDirectoryRedis.create(dataCreateDirectoryVideo, user.getLogin(), user.getRole().getAuthority());
        serviceCreateDirectoryRedis.create(dataCreateDirectoryMusic, user.getLogin(), user.getRole().getAuthority());
        serviceCreateDirectoryRedis.create(dataCreateDirectoryImages, user.getLogin(), user.getRole().getAuthority());
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public UserAlreadyExistMsg handleExceptionAlready(@NotNull UserAlreadyExistException error) {
        return new UserAlreadyExistMsg(error.getMessage());
    }

}
