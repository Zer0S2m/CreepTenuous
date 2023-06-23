package com.zer0s2m.creeptenuous.api.documentation.controllers;

import com.zer0s2m.creeptenuous.common.http.ResponseUserApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
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
            tags = { "User" },
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

}
