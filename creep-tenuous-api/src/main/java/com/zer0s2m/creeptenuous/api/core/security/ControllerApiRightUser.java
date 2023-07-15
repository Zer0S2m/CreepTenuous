package com.zer0s2m.creeptenuous.api.core.security;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiRightUserDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.containers.ContainerInfoFileSystemObject;
import com.zer0s2m.creeptenuous.common.data.DataCreateRightUserApi;
import com.zer0s2m.creeptenuous.common.data.DataDeleteRightUserApi;
import com.zer0s2m.creeptenuous.common.data.DataViewGrantedRightsApi;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.common.http.ResponseAllGrantedRightsApi;
import com.zer0s2m.creeptenuous.common.http.ResponseCreateRightUserApi;
import com.zer0s2m.creeptenuous.common.exceptions.ChangeRightsYourselfException;
import com.zer0s2m.creeptenuous.common.exceptions.NoExistsFileSystemObjectRedisException;
import com.zer0s2m.creeptenuous.common.exceptions.NoExistsRightException;
import com.zer0s2m.creeptenuous.common.exceptions.messages.ExceptionAddRightsYourselfMsg;
import com.zer0s2m.creeptenuous.common.exceptions.messages.ExceptionNoExistsFileSystemObjectRedisMsg;
import com.zer0s2m.creeptenuous.common.exceptions.messages.ExceptionNoExistsRightMsg;
import com.zer0s2m.creeptenuous.common.http.ResponseGrantedRightsApi;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.services.resources.ServiceRedisManagerResources;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceFileSystemRedisManagerRightsAccess;
import com.zer0s2m.creeptenuous.security.jwt.exceptions.messages.UserNotFoundMsg;
import com.zer0s2m.creeptenuous.common.utils.WalkDirectoryInfo;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@V1APIRestController
public class ControllerApiRightUser implements ControllerApiRightUserDoc {

    private final ServiceManagerRights serviceManagerRights;

    private final BaseServiceFileSystemRedisManagerRightsAccess serviceFileSystemRedis;

    private final ServiceRedisManagerResources serviceRedisManagerResources;

    @Autowired
    public ControllerApiRightUser(
            ServiceManagerRights serviceManagerRights,
            BaseServiceFileSystemRedisManagerRightsAccess baseServiceFileSystemRedis,
            ServiceRedisManagerResources serviceRedisManagerResources) {
        this.serviceManagerRights = serviceManagerRights;
        this.serviceFileSystemRedis = baseServiceFileSystemRedis;
        this.serviceRedisManagerResources = serviceRedisManagerResources;
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
    @PostMapping("/user/global/right")
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
        serviceManagerRights.setDirectoryPassAccess(data.systemName(), data.loginUser());

        return new ResponseCreateRightUserApi(data.systemName(), data.loginUser(), operation);
    }

    /**
     * Add rights for user on filesystem target - all directory content
     * @param data Data to add
     * @param accessToken raw JWT access token
     * @throws UserNotFoundException the user does not exist in the system
     * @throws NoExistsFileSystemObjectRedisException the file system object was not found in the database.
     * @throws IOException signals that an I/O exception of some sort has occurred
     * @throws ChangeRightsYourselfException change rights over the interaction of file system objects to itself
     */
    @Override
    @PostMapping("/user/global/right/directory")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void addComplex(final @Valid @RequestBody @NotNull DataCreateRightUserApi data,
                           @RequestHeader(name = "Authorization") String accessToken) throws UserNotFoundException,
            NoExistsFileSystemObjectRedisException, IOException, ChangeRightsYourselfException {
        serviceFileSystemRedis.setAccessToken(accessToken);
        serviceFileSystemRedis.checkRights(data.systemName());

        serviceManagerRights.setIsWillBeCreated(false);
        serviceManagerRights.setAccessClaims(accessToken);
        serviceManagerRights.isExistsUser(data.loginUser());
        serviceManagerRights.isExistsFileSystemObject(data.systemName());
        OperationRights operation = OperationRights.valueOf(data.right());

        DirectoryRedis directoryRedis = serviceRedisManagerResources.getResourceDirectoryRedis(data.systemName());
        if (directoryRedis == null) {
            throw new NoExistsFileSystemObjectRedisException();
        }

        Path sourceDirectory = Path.of(directoryRedis.getPathDirectory());

        List<ContainerInfoFileSystemObject> attached = WalkDirectoryInfo.walkDirectory(sourceDirectory);
        List<String> namesFileSystemObject = attached
                .stream()
                .map(ContainerInfoFileSystemObject::nameFileSystemObject)
                .toList();

        serviceFileSystemRedis.checkRights(namesFileSystemObject);

        serviceManagerRights.addRight(
                serviceManagerRights.buildObj(namesFileSystemObject, data.loginUser(), operation), operation);
        serviceManagerRights.setDirectoryPassAccess(data.systemName(), data.loginUser());
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
    @DeleteMapping("/user/global/right")
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
     * Delete rights for a user on a file system target
     *
     * @param data        data to delete
     * @param accessToken raw JWT access token
     * @throws UserNotFoundException                  the user does not exist in the system
     * @throws NoExistsFileSystemObjectRedisException the file system object was not found in the database.
     * @throws IOException                            if an I/O error occurs or the parent directory does not exist
     * @throws ChangeRightsYourselfException          change rights over the interaction of file system objects to itself
     * @throws NoExistsRightException                 The right was not found in the database.
     *                                                Or is {@literal null} {@link NullPointerException}
     */
    @Override
    @DeleteMapping("/user/global/right/directory")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteComplex(final @Valid @RequestBody @NotNull DataDeleteRightUserApi data,
                              @RequestHeader(name = "Authorization") String accessToken)
            throws UserNotFoundException, NoExistsFileSystemObjectRedisException,
            IOException, ChangeRightsYourselfException, NoExistsRightException {
        serviceFileSystemRedis.setAccessToken(accessToken);
        serviceFileSystemRedis.checkRights(data.systemName());

        serviceManagerRights.setIsWillBeCreated(false);
        serviceManagerRights.setAccessClaims(accessToken);
        serviceManagerRights.isExistsUser(data.loginUser());
        serviceManagerRights.isExistsFileSystemObject(data.systemName());
        OperationRights operation = OperationRights.valueOf(data.right());

        DirectoryRedis directoryRedis = serviceRedisManagerResources.getResourceDirectoryRedis(data.systemName());
        if (directoryRedis == null) {
            throw new NoExistsFileSystemObjectRedisException();
        }

        Path sourceDirectory = Path.of(directoryRedis.getPathDirectory());

        List<ContainerInfoFileSystemObject> attached = WalkDirectoryInfo.walkDirectory(sourceDirectory);
        List<String> namesFileSystemObject = attached
                .stream()
                .map(ContainerInfoFileSystemObject::nameFileSystemObject)
                .toList();

        serviceFileSystemRedis.checkRights(namesFileSystemObject);

        serviceManagerRights.deleteRight(
                serviceManagerRights.getObj(namesFileSystemObject, data.loginUser()), operation);
    }

    /**
     * Get permission data for a file object
     * @param data data for obtaining granted rights to a file object
     * @param accessToken raw JWT access token
     * @return granted rights
     * @throws NoExistsFileSystemObjectRedisException the file system object was not found in the database.
     */
    @Override
    @PostMapping("/user/global/right/list")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseGrantedRightsApi viewGrantedRights(
            final @Valid @RequestBody @NotNull DataViewGrantedRightsApi data,
            @RequestHeader(name = "Authorization") String accessToken) throws NoExistsFileSystemObjectRedisException {
        serviceFileSystemRedis.setAccessToken(accessToken);
        serviceFileSystemRedis.checkRights(data.systemName());

        serviceManagerRights.setIsWillBeCreated(false);
        serviceManagerRights.setAccessClaims(accessToken);
        serviceManagerRights.isExistsFileSystemObject(data.systemName());

        return new ResponseGrantedRightsApi(
                data.systemName(),
                serviceManagerRights.getGrantedRight(data.systemName()));
    }

    /**
     * Get information about all issued rights to all objects
     * @param accessToken raw JWT access token
     * @return granted all rights
     */
    @Override
    @GetMapping("/user/global/right/list-all")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseAllGrantedRightsApi viewAllGrantedRights(
            @RequestHeader(name = "Authorization") String accessToken) {
        serviceManagerRights.setIsWillBeCreated(false);
        serviceManagerRights.setAccessClaims(accessToken);
        return new ResponseAllGrantedRightsApi(
                new ArrayList<>(serviceManagerRights.getGrantedRight()));
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
