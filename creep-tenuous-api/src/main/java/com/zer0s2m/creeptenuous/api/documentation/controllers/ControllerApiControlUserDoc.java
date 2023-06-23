package com.zer0s2m.creeptenuous.api.documentation.controllers;

import com.zer0s2m.creeptenuous.common.data.DataDeleteUserApi;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.common.http.ResponseUserApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

public interface ControllerApiControlUserDoc {

    /**
     * Get all users in the system
     * @return users
     */
    @Operation(
            method = "GET",
            summary = "Get users",
            description = "Get all users in the system",
            tags = { "User", "User control" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(
                                                    implementation = ResponseUserApi.class
                                            )
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(ref = "#/components/schemas/Unauthorized")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    )
            }
    )
    List<ResponseUserApi> getAllUsers();

    /**
     * Removing a user from the system by his login
     * @param data data to delete
     * @throws UserNotFoundException user does not exist in the system
     */
    @Operation(
            method = "DELETE",
            summary = "Delete user",
            description = "Get all users in the system",
            tags = { "User", "User control" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data to download a directory",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataDeleteUserApi.class)
                            )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successfully removed"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(ref = "#/components/schemas/Unauthorized")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found user",
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
    void deleteUserByLogin(final DataDeleteUserApi data) throws UserNotFoundException;

}
