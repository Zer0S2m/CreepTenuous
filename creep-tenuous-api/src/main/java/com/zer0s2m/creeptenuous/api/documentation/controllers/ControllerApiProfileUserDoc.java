package com.zer0s2m.creeptenuous.api.documentation.controllers;

import com.zer0s2m.creeptenuous.common.data.DataControlFileObjectsExclusionApi;
import com.zer0s2m.creeptenuous.common.data.DataIsDeletingFileObjectApi;
import com.zer0s2m.creeptenuous.common.data.DataTransferredUserApi;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundException;
import com.zer0s2m.creeptenuous.common.exceptions.UploadAvatarForUserException;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.common.http.ResponseUploadAvatarUserApi;
import com.zer0s2m.creeptenuous.common.http.ResponseUserApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ControllerApiProfileUserDoc {

    /**
     * Get info user by JWT token
     * @return user info
     */
    @Operation(
            method = "GET",
            summary = "Get user info",
            description = "User info by JWT token",
            tags = { "User" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseUserApi.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(ref = "#/components/schemas/Unauthorized")
                            )
                    )
            }
    )
    ResponseUserApi profile(@Parameter(hidden = true) String accessToken);

    /**
     * Set setting for user - deleting a user if it is deleted
     * @param data setting data
     * @param accessToken raw access JWT token
     * @throws UserNotFoundException not exists user
     */
    @Operation(
            method = "PATCH",
            summary = "Set setting - deleting file objects",
            description = "Set setting - deleting file objects for a user if he is deleted",
            tags = { "User" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Setting data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataIsDeletingFileObjectApi.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successful",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(ref = "#/components/schemas/Unauthorized")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Not found user", value = "{" +
                                                    "\"message\": \"User is not found.\"," +
                                                    "\"statusCode\": 404" +
                                                    "}"
                                            ),
                                    })
                    )
            }
    )
    void setIsDeletingFileObjectsSettings(
            final DataIsDeletingFileObjectApi data,
            @Parameter(hidden = true) String accessToken) throws UserNotFoundException;

    /**
     * Set setting - transfer file objects to designated user
     * @param data setting data
     * @param accessToken raw access JWT token
     * @throws UserNotFoundException not exists user
     */
    @Operation(
            method = "PATCH",
            summary = "Set setting - transfer file objects to designated user",
            description = "Set setting - transfer file objects to the assigned user if the user is deleted",
            tags = { "User" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Setting data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataTransferredUserApi.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successful",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(ref = "#/components/schemas/Unauthorized")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Not found user", value = "{" +
                                                    "\"message\": \"User is not found.\"," +
                                                    "\"statusCode\": 404" +
                                                    "}"
                                            ),
                                    })
                    )
            }
    )
    void setTransferredUserId(
            final DataTransferredUserApi data, @Parameter(hidden = true) String accessToken)
            throws UserNotFoundException;

    /**
     * Set file objects to exclusions when deleting a user and then allocating them
     * @param data data to set
     * @param accessToken raw access JWT token
     * @throws UserNotFoundException not exists user
     * @throws NotFoundException not exists file objects
     */
    @Operation(
            method = "DELETE",
            summary = "Set file objects to exclusions when deleting a user and then allocating them",
            description = "Set file objects to exclusions when deleting a user and then allocating them",
            tags = { "User" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successful",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(ref = "#/components/schemas/Unauthorized")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Not found user", value = "{" +
                                                    "\"message\": \"User is not found.\"," +
                                                    "\"statusCode\": 404" +
                                                    "}"
                                            ),
                                            @ExampleObject(name = "Not found objects", value = "{" +
                                                    "\"message\": \"Not found file objects.\"," +
                                                    "\"statusCode\": 404" +
                                                    "}"
                                            ),
                                    })
                    )
            }
    )
    void setFileObjectsExclusion(
            final DataControlFileObjectsExclusionApi data, @Parameter(hidden = true) String accessToken)
            throws NotFoundException;

    /**
     * Remove file objects from exclusion on user deletion and then allocate them
     * @param data data to deleted
     * @param accessToken raw access JWT token
     * @throws UserNotFoundException not exists user
     */
    @Operation(
            method = "DELETE",
            summary = "Remove file objects from exclusion on user deletion and then allocate them",
            description = "Remove file objects from exclusion on user deletion and then allocate them",
            tags = { "User" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successful",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(ref = "#/components/schemas/Unauthorized")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Not found user", value = "{" +
                                                    "\"message\": \"User is not found.\"," +
                                                    "\"statusCode\": 404" +
                                                    "}"
                                            )
                                    })
                    )
            }
    )
    void deleteFileObjectsExclusion(
            final DataControlFileObjectsExclusionApi data, @Parameter(hidden = true) String accessToken)
            throws NotFoundException;

    /**
     * Upload an avatar for the user by his login.
     * @param file Uploaded file.
     * @param accessToken Raw access JWT token.
     * @return Upload avatar.
     * @throws UploadAvatarForUserException Exceptions for loading an avatar for a user.
     * @throws IOException Signals that an I/O exception of some sort has occurred.
     */
    @Operation(
            method = "POST",
            summary = "Upload an avatar for a user",
            description = "Upload an avatar for a user",
            tags = { "User" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @RequestBody(
                    description = "User avatar download data",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(
                                    name = "avatar",
                                    description = "Image as avatar",
                                    requiredMode = Schema.RequiredMode.REQUIRED,
                                    type = "string",
                                    format = "binary"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ResponseUploadAvatarUserApi.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(ref = "#/components/schemas/Unauthorized")
                            )
                    )
            }
    )
    ResponseUploadAvatarUserApi uploadAvatar(MultipartFile file, @Parameter(hidden = true) String accessToken)
            throws UploadAvatarForUserException, IOException;

    /**
     * Removing an avatar for a user.
     * @param accessToken Raw access JWT token.
     * @throws UserNotFoundException he user does not exist in the system.
     * @throws IOException Signals that an I/O exception to some sort has occurred.
     */
    @Operation(
            method = "DELETE",
            summary = "Removing an avatar for a user",
            description = "Removing an avatar for a user",
            tags = { "User" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successful",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(ref = "#/components/schemas/Unauthorized")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Not found user", value = "{" +
                                                    "\"message\": \"User is not found.\"," +
                                                    "\"statusCode\": 404" +
                                                    "}"
                                            )
                                    })
                    )
            }
    )
    void deleteAvatar(@Parameter(hidden = true) String accessToken) throws UserNotFoundException, IOException;

}
