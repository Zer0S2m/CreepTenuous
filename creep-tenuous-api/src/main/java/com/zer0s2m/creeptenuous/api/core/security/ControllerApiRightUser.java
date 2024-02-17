package com.zer0s2m.creeptenuous.api.core.security;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiRightUserDoc;
import com.zer0s2m.creeptenuous.api.annotation.V1APIRestController;
import com.zer0s2m.creeptenuous.common.containers.ContainerInfoFileSystemObject;
import com.zer0s2m.creeptenuous.common.data.DataCreateRightUserApi;
import com.zer0s2m.creeptenuous.common.data.DataDeleteRightUserApi;
import com.zer0s2m.creeptenuous.common.data.DataViewGrantedRightsApi;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.exceptions.*;
import com.zer0s2m.creeptenuous.common.http.ResponseAllGrantedRightsApi;
import com.zer0s2m.creeptenuous.common.containers.ContainerAssignedRights;
import com.zer0s2m.creeptenuous.common.http.ResponseCreateRightUserApi;
import com.zer0s2m.creeptenuous.common.http.ResponseError;
import com.zer0s2m.creeptenuous.common.http.ResponseGrantedRightsApi;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.services.resources.ServiceRedisManagerResources;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceFileSystemRedisManagerRightsAccess;
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
import java.util.UUID;

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

    private @NotNull List<OperationRights> convertStrToEnumOperationRight(@NotNull List<String> rights) {
        List<OperationRights> operations = new ArrayList<>();
        rights.forEach(right -> operations.add(OperationRights.valueOf(right)));
        return operations;
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
        List<OperationRights> operations = convertStrToEnumOperationRight(data.right());

        serviceManagerRights.addRight(
                serviceManagerRights.buildObj(data.systemName(), data.loginUser(), operations));
        serviceManagerRights.setDirectoryPassAccess(data.systemName(), data.loginUser());

        return new ResponseCreateRightUserApi(data.systemName(), data.loginUser(), operations);
    }

    /**
     * Add rights for user on filesystem target - all directory content
     * @param data Data to add
     * @param accessToken raw JWT access token
     * @throws UserNotFoundException the user does not exist in the system
     * @throws NoExistsFileSystemObjectRedisException the file system object was not found in the database.
     * @throws IOException signals that an I/O exception to some sort has occurred
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
        List<OperationRights> operations = convertStrToEnumOperationRight(data.right());

        DirectoryRedis directoryRedis = serviceRedisManagerResources.getResourceDirectoryRedis(data.systemName());
        if (directoryRedis == null) {
            throw new NoExistsFileSystemObjectRedisException();
        }

        Path sourceDirectory = Path.of(directoryRedis.getPath());

        List<ContainerInfoFileSystemObject> attached = WalkDirectoryInfo.walkDirectory(sourceDirectory);
        List<String> namesFileSystemObject = attached
                .stream()
                .map(ContainerInfoFileSystemObject::nameFileSystemObject)
                .toList();

        serviceFileSystemRedis.checkRights(namesFileSystemObject);

        serviceManagerRights.addRight(
                serviceManagerRights.buildObj(namesFileSystemObject, data.loginUser(), operations), operations);
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
                convertStrToEnumOperationRight(data.right()));
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
        List<OperationRights> operations = convertStrToEnumOperationRight(data.right());

        DirectoryRedis directoryRedis = serviceRedisManagerResources.getResourceDirectoryRedis(data.systemName());
        if (directoryRedis == null) {
            throw new NoExistsFileSystemObjectRedisException();
        }

        Path sourceDirectory = Path.of(directoryRedis.getPath());

        List<ContainerInfoFileSystemObject> attached = WalkDirectoryInfo.walkDirectory(sourceDirectory);
        List<String> namesFileSystemObject = attached
                .stream()
                .map(ContainerInfoFileSystemObject::nameFileSystemObject)
                .toList();

        serviceFileSystemRedis.checkRights(namesFileSystemObject);

        serviceManagerRights.deleteRight(
                serviceManagerRights.getObj(namesFileSystemObject, data.loginUser()), operations);
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
                serviceRedisManagerResources
                        .getRealNameFileObject(data.systemName())
                        .orElse(null),
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
     * Get information about assigned rights.
     *
     * @param systemName  System name of the file object.
     * @param accessToken Raw JWT access token.
     * @return Information about assigned rights.
     * @throws NoExistsFileSystemObjectRedisException The file system object was not found in the database.
     * @throws NoRightsRedisException                 Insufficient rights to perform the operation.
     * @throws ViewAssignedRightsYourselfException    Unable to view assigned rights to your file object.
     */
    @Override
    @GetMapping("/user/global/right/assigned")
    @ResponseStatus(code = HttpStatus.OK)
    public ContainerAssignedRights viewAssignedRights(
            final @RequestParam("file") @NotNull UUID systemName,
            @RequestHeader(name = "Authorization") String accessToken)
            throws NoExistsFileSystemObjectRedisException, NoRightsRedisException,
            ViewAssignedRightsYourselfException {
        serviceFileSystemRedis.setAccessToken(accessToken);
        serviceFileSystemRedis.setIsException(false);

        serviceManagerRights.setIsWillBeCreated(false);
        serviceManagerRights.setAccessClaims(accessToken);
        serviceManagerRights.isExistsFileSystemObject(systemName.toString());

        if (!serviceFileSystemRedis.checkRights(systemName.toString())) {
            serviceManagerRights.checkRightsByOperation(
                    OperationRights.ANALYSIS, systemName.toString());
        } else {
            throw new ViewAssignedRightsYourselfException("Unable to view assigned rights to your file object.");
        }

        return serviceManagerRights.getAssignedRight(systemName.toString());
    }

    /**
     * Error andler
     * @param error the file system object was not found in the database
     * @return error message for user
     */
    @ExceptionHandler({NoExistsFileSystemObjectRedisException.class})
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ResponseError handleExceptionNoExistsFileSystemObjectRedis(
            @NotNull NoExistsFileSystemObjectRedisException error) {
        return new ResponseError(error.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    /**
     * Error handler
     * @param error the user does not exist in the system
     * @return error message for user
     */
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ResponseError handleExceptionNotIsExistsUser(@NotNull UserNotFoundException error) {
        return new ResponseError(error.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    /**
     * Error handler
     * @param error The right was not found in the database. Or is {@literal null} {@link NullPointerException}
     * @return error message for user
     */
    @ExceptionHandler(NoExistsRightException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ResponseError handleExceptionNoExistsRight(@NotNull NoExistsRightException error) {
        return new ResponseError(error.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    /**
     * Error handler
     * @param error adding rights over the interaction of file system objects to itself
     * @return error message for user
     */
    @ExceptionHandler(ChangeRightsYourselfException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ResponseError handleExceptionAddRightsYourself(
            @NotNull ChangeRightsYourselfException error) {
        return new ResponseError(error.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Error handler.
     * @param error Unable to view assigned rights to your file object.
     * @return Error message for user.
     */
    @ExceptionHandler(ViewAssignedRightsYourselfException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ResponseError handleExceptionViewAssignedRightsYourself(
            @NotNull ViewAssignedRightsYourselfException error) {
        return new ResponseError(error.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

}
