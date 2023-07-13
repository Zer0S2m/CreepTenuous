package com.zer0s2m.creeptenuous.api.documentation.controllers;

import com.zer0s2m.creeptenuous.common.data.DataCreateRightUserApi;
import com.zer0s2m.creeptenuous.common.data.DataDeleteRightUserApi;
import com.zer0s2m.creeptenuous.common.data.DataViewGrantedRightsApi;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.common.http.ResponseCreateRightUserApi;
import com.zer0s2m.creeptenuous.common.exceptions.ChangeRightsYourselfException;
import com.zer0s2m.creeptenuous.common.exceptions.NoExistsFileSystemObjectRedisException;
import com.zer0s2m.creeptenuous.common.exceptions.NoExistsRightException;
import com.zer0s2m.creeptenuous.common.http.ResponseGrantedRightsApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import java.io.IOException;

public interface ControllerApiRightUserDoc {

    /**
     * Add rights for a user on a file system target
     * @param data Data to add
     * @param accessToken raw JWT access token
     * @return created data
     * @throws NoExistsFileSystemObjectRedisException the file system object was not found in the database.
     * @throws UserNotFoundException the user does not exist in the system
     * @throws ChangeRightsYourselfException change rights over the interaction of file system objects to itself
     */
    @Operation(
            method = "POST",
            summary = "Adding a right",
            description = "Adding the right to interact with the file system object",
            tags = { "User", "Right" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @RequestBody(
                    description = "Data to adding a right",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataCreateRightUserApi.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Successful rights added",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseCreateRightUserApi.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Cannot add rights", value ="{" +
                                                    "\"message\": \"You cannot change rights to yourself\"," +
                                                    "\"statusCode\": 400" +
                                                    "}"
                                            )
                                    })
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Not found system object", value ="{" +
                                                    "\"message\": \"Not found file system object\"," +
                                                    "\"statusCode\": 404" +
                                                    "}"
                                            ),
                                            @ExampleObject(name = "Not found user", value = "{" +
                                                    "\"message\": \"User is not found.\"," +
                                                    "\"statusCode\": 404" +
                                                    "}"
                                    ),
                            })
                    )
            }
    )
    ResponseCreateRightUserApi add(final DataCreateRightUserApi data, @Parameter(hidden = true) String accessToken)
            throws NoExistsFileSystemObjectRedisException, UserNotFoundException, ChangeRightsYourselfException;

    /**
     * Add rights for user on filesystem target - all directory content
     * @param data Data to add
     * @param accessToken raw JWT access token
     * @throws UserNotFoundException the user does not exist in the system
     * @throws NoExistsFileSystemObjectRedisException the file system object was not found in the database.
     * @throws IOException signals that an I/O exception of some sort has occurred
     * @throws ChangeRightsYourselfException change rights over the interaction of file system objects to itself
     */
    @Operation(
            method = "POST",
            summary = "Adding a right",
            description = "Adding the right to interact with the file system object - to the entire content " +
                    "of the directory",
            tags = { "User", "Right" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @RequestBody(
                    description = "Data to adding a right",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataCreateRightUserApi.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Successful rights added",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseCreateRightUserApi.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Cannot add rights", value ="{" +
                                                    "\"message\": \"You cannot change rights to yourself\"," +
                                                    "\"statusCode\": 400" +
                                                    "}"
                                            )
                                    })
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Not found system object", value ="{" +
                                                    "\"message\": \"Not found file system object\"," +
                                                    "\"statusCode\": 404" +
                                                    "}"
                                            ),
                                            @ExampleObject(name = "Not found user", value = "{" +
                                                    "\"message\": \"User is not found.\"," +
                                                    "\"statusCode\": 404" +
                                                    "}"
                                            ),
                                    })
                    )
            }
    )
    void addComplex(final DataCreateRightUserApi data, @Parameter(hidden = true) String accessToken)
            throws UserNotFoundException, NoExistsFileSystemObjectRedisException, IOException,
            ChangeRightsYourselfException;

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
    @Operation(
            method = "DELETE",
            summary = "Deleting a right",
            description = "Deleting the right to interact with the file system object",
            tags = { "User", "Right" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @RequestBody(
                    description = "Data to deleting a right",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataDeleteRightUserApi.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successful rights deleting"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Cannot delete rights", value ="{" +
                                                    "\"message\": \"You cannot change rights to yourself\"," +
                                                    "\"statusCode\": 400" +
                                                    "}"
                                            )
                                    })
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Not found system object", value ="{" +
                                                    "\"message\": \"Not found file system object\"," +
                                                    "\"statusCode\": 404" +
                                                    "}"
                                            ),
                                            @ExampleObject(name = "Not found user", value = "{" +
                                                    "\"message\": \"User is not found.\"," +
                                                    "\"statusCode\": 404" +
                                                    "}"
                                            ),
                                            @ExampleObject(name = "Not found right", value = "{" +
                                                    "\"message\": \"Not found right.\"," +
                                                    "\"statusCode\": 404" +
                                                    "}"
                                            )
                                    })
                    )
            }
    )
    void delete(final DataDeleteRightUserApi data, @Parameter(hidden = true) String accessToken)
            throws UserNotFoundException, NoExistsFileSystemObjectRedisException,
            ChangeRightsYourselfException, NoExistsRightException;

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
    @Operation(
            method = "DELETE",
            summary = "Deleting a right",
            description = "Deleting the right to interact with the file system object - to the entire content " +
                    "of the directory",
            tags = { "User", "Right" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @RequestBody(
                    description = "Data to deleting a right",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataDeleteRightUserApi.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successful rights deleting"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Cannot delete rights", value ="{" +
                                                    "\"message\": \"You cannot change rights to yourself\"," +
                                                    "\"statusCode\": 400" +
                                                    "}"
                                            )
                                    })
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Not found system object", value ="{" +
                                                    "\"message\": \"Not found file system object\"," +
                                                    "\"statusCode\": 404" +
                                                    "}"
                                            ),
                                            @ExampleObject(name = "Not found user", value = "{" +
                                                    "\"message\": \"User is not found.\"," +
                                                    "\"statusCode\": 404" +
                                                    "}"
                                            ),
                                            @ExampleObject(name = "Not found right", value = "{" +
                                                    "\"message\": \"Not found right.\"," +
                                                    "\"statusCode\": 404" +
                                                    "}"
                                            )
                                    })
                    )
            }
    )
    void deleteComplex(final DataDeleteRightUserApi data, @Parameter(hidden = true) String accessToken)
            throws UserNotFoundException, NoExistsFileSystemObjectRedisException, IOException,
            ChangeRightsYourselfException, NoExistsRightException ;

    /**
     * Get permission data for a file object
     * @param data data for obtaining granted rights to a file object
     * @param accessToken raw JWT access token
     * @return granted rights
     * @throws NoExistsFileSystemObjectRedisException the file system object was not found in the database.
     */
    @Operation(
            method = "POST",
            summary = "Get information about granted rights for one object",
            description = "Get information about granted rights for one object for system users",
            tags = { "User", "Right" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @RequestBody(
                    description = "Data to deleting a right",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataViewGrantedRightsApi.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful getting",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseGrantedRightsApi.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Not found system object", value ="{" +
                                                    "\"message\": \"Not found file system object\"," +
                                                    "\"statusCode\": 404" +
                                                    "}"
                                            )
                                    })
                    )
            }
    )
    ResponseGrantedRightsApi viewGrantedRights(final DataViewGrantedRightsApi data,
                                               @Parameter(hidden = true) String accessToken)
            throws NoExistsFileSystemObjectRedisException;

}
