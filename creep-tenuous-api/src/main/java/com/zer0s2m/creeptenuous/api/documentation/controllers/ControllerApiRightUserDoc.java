package com.zer0s2m.creeptenuous.api.documentation.controllers;

import com.zer0s2m.creeptenuous.common.data.DataCreateRightUserApi;
import com.zer0s2m.creeptenuous.common.data.DataDeleteRightUserApi;
import com.zer0s2m.creeptenuous.common.http.ResponseCreateRightUserApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

public interface ControllerApiRightUserDoc {

    /**
     * Add rights for a user on a file system target
     * @param data data to add
     * @param accessToken raw JWT access token
     * @return created data
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
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
            }
    )
    ResponseCreateRightUserApi add(final DataCreateRightUserApi data, @Parameter(hidden = true) String accessToken);

    /**
     * Delete rights for a user on a file system target
     * @param data data to delete
     * @param accessToken raw JWT access token
     */
    @Operation(
            method = "POST",
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
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
            }
    )
    void delete(final DataDeleteRightUserApi data, @Parameter(hidden = true) String accessToken);
}
