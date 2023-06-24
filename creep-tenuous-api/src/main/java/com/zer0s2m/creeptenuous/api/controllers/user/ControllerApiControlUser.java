package com.zer0s2m.creeptenuous.api.controllers.user;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiControlUserDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.data.DataDeleteUserApi;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.common.http.ResponseUserApi;
import com.zer0s2m.creeptenuous.events.UserEventPublisher;
import com.zer0s2m.creeptenuous.services.user.ServiceControlUser;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@V1APIRestController
public class ControllerApiControlUser implements ControllerApiControlUserDoc {

    private final ServiceControlUser serviceControlUser;

    private final UserEventPublisher userEventPublisher;

    @Autowired
    public ControllerApiControlUser(ServiceControlUser serviceControlUser, UserEventPublisher userEventPublisher) {
        this.serviceControlUser = serviceControlUser;
        this.userEventPublisher = userEventPublisher;
    }

    /**
     * Get all users in the system
     * @return users
     */
    @Override
    @GetMapping("/user/control/list")
    @ResponseStatus(code = HttpStatus.OK)
    @RolesAllowed("ROLE_ADMIN")
    public List<ResponseUserApi> getAllUsers() {
        return serviceControlUser.getAllUsers()
                .stream()
                .map(user -> new ResponseUserApi(user.getLogin(), user.getEmail(),
                        user.getName(), Set.of(user.getRole())))
                .collect(Collectors.toList());
    }

    /**
     * Removing a user from the system by his login
     * @param data data to delete
     * @throws UserNotFoundException user does not exist in the system
     */
    @Override
    @DeleteMapping("/user/control/delete")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @RolesAllowed("ROLE_ADMIN")
    public void deleteUserByLogin(final @Valid @RequestBody @NotNull DataDeleteUserApi data)
            throws UserNotFoundException {
        serviceControlUser.deleteUser(data.login());
        userEventPublisher.publishDelete();
    }

}
