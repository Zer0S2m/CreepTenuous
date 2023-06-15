package com.zer0s2m.creeptenuous.api.core.security;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiRightUserDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.data.DataCreateRightUserApi;
import com.zer0s2m.creeptenuous.common.data.DataDeleteRightUserApi;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.common.http.ResponseCreateRightUserApi;
import com.zer0s2m.creeptenuous.redis.exceptions.ChangeRightsYourselfException;
import com.zer0s2m.creeptenuous.redis.exceptions.NoExistsFileSystemObjectRedisException;
import com.zer0s2m.creeptenuous.redis.exceptions.NoExistsRightException;
import com.zer0s2m.creeptenuous.redis.exceptions.messages.ExceptionAddRightsYourselfMsg;
import com.zer0s2m.creeptenuous.redis.exceptions.messages.ExceptionNoExistsFileSystemObjectRedisMsg;
import com.zer0s2m.creeptenuous.redis.exceptions.messages.ExceptionNoExistsRightMsg;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceFileSystemRedisManagerRightsAccess;
import com.zer0s2m.creeptenuous.security.jwt.exceptions.messages.UserNotFoundMsg;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@V1APIRestController
public class ControllerApiRightUser implements ControllerApiRightUserDoc {

    private final ServiceManagerRights serviceManagerRights;

    private final BaseServiceFileSystemRedisManagerRightsAccess serviceFileSystemRedis;

    @Autowired
    public ControllerApiRightUser(
            ServiceManagerRights serviceManagerRights,
            BaseServiceFileSystemRedisManagerRightsAccess baseServiceFileSystemRedis) {
        this.serviceManagerRights = serviceManagerRights;
        this.serviceFileSystemRedis = baseServiceFileSystemRedis;
    }

    /**
     * Add rights for a user on a file system target
     * @param data Data to add
     * @param accessToken raw JWT access token
     * @return created data
     * @throws NoExistsFileSystemObjectRedisException the file system object was not found in the database.
     * @throws UserNotFoundException the user does not exist in the system
     * @throws ChangeRightsYourselfException change rights over the interaction of file system objects to itself
     */
    @Override
    @PostMapping("/user/right")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseCreateRightUserApi add(final @Valid @RequestBody @NotNull DataCreateRightUserApi data,
                                          @RequestHeader(name = "Authorization") String accessToken)
            throws NoExistsFileSystemObjectRedisException, UserNotFoundException, ChangeRightsYourselfException {
        serviceFileSystemRedis.setAccessToken(accessToken);
        serviceFileSystemRedis.checkRights(data.systemName());

        serviceManagerRights.setIsWillBeCreated(false);
        serviceManagerRights.setAccessClaims(accessToken);
        serviceManagerRights.isExistsUser(data.loginUser());
        serviceManagerRights.isExistsFileSystemObject(data.systemName());
        OperationRights operation = OperationRights.valueOf(data.right());

        serviceManagerRights.addRight(
                serviceManagerRights.buildObj(data.systemName(), data.loginUser(), operation));

        return new ResponseCreateRightUserApi(data.systemName(), data.loginUser(), operation);
    }

    /**
     * Delete rights for a user on a file system target
     * @param data data to delete
     * @param accessToken raw JWT access token
     * @throws NoExistsFileSystemObjectRedisException the file system object was not found in the database.
     * @throws UserNotFoundException the user does not exist in the system
     * @throws ChangeRightsYourselfException change rights over the interaction of file system objects to itself
     * @throws NoExistsRightException The right was not found in the database.
     *                                Or is {@literal null} {@link NullPointerException}
     */
    @Override
    @DeleteMapping("/user/right")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(
            final @Valid @RequestBody @NotNull DataDeleteRightUserApi data,
            @RequestHeader(name = "Authorization") String accessToken
    ) throws UserNotFoundException, NoExistsFileSystemObjectRedisException, ChangeRightsYourselfException,
            NoExistsRightException {
        serviceFileSystemRedis.setAccessToken(accessToken);
        serviceFileSystemRedis.checkRights(data.systemName());

        serviceManagerRights.setIsWillBeCreated(false);
        serviceManagerRights.setAccessClaims(accessToken);
        serviceManagerRights.isExistsUser(data.loginUser());
        serviceManagerRights.isExistsFileSystemObject(data.systemName());

        serviceManagerRights.deleteRight(
                serviceManagerRights.getObj(data.systemName(), data.loginUser()),
                OperationRights.valueOf(data.right()));
    }

    /**
     * Error andler
     * @param error the file system object was not found in the database
     * @return error message for user
     */
    @ExceptionHandler({NoExistsFileSystemObjectRedisException.class})
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ExceptionNoExistsFileSystemObjectRedisMsg handleExceptionNoExistsFileSystemObjectRedis(
            @NotNull NoExistsFileSystemObjectRedisException error) {
        return new ExceptionNoExistsFileSystemObjectRedisMsg(error.getMessage());
    }

    /**
     * Error handler
     * @param error the user does not exist in the system
     * @return error message for user
     */
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public UserNotFoundMsg handleExceptionNotIsExistsUser(@NotNull UserNotFoundException error) {
        return new UserNotFoundMsg(error.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    /**
     * Error handler
     * @param error The right was not found in the database. Or is {@literal null} {@link NullPointerException}
     * @return error message for user
     */
    @ExceptionHandler(NoExistsRightException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ExceptionNoExistsRightMsg handleExceptionNoExistsRight(@NotNull NoExistsRightException error) {
        return new ExceptionNoExistsRightMsg(error.getMessage());
    }

    /**
     * Error handler
     * @param error adding rights over the interaction of file system objects to itself
     * @return error message for user
     */
    @ExceptionHandler(ChangeRightsYourselfException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ExceptionAddRightsYourselfMsg handleExceptionAddRightsYourself(
            @NotNull ChangeRightsYourselfException error) {
        return new ExceptionAddRightsYourselfMsg(error.getMessage());
    }

}
