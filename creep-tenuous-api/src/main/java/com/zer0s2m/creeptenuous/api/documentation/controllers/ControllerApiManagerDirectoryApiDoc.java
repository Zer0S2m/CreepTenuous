package com.zer0s2m.creeptenuous.api.documentation.controllers;

import com.zer0s2m.creeptenuous.common.data.DataManagerDirectoryApi;
import com.zer0s2m.creeptenuous.common.exceptions.NotValidLevelDirectoryException;
import com.zer0s2m.creeptenuous.common.http.ResponseManagerDirectoryApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.io.IOException;

public interface ControllerApiManagerDirectoryApiDoc {

    /**
     * Manager directory - get all directories by level
     * @param data data manager directory
     * @return result manager build info in directory
     * @throws IOException if an I/O error occurs or the parent directory does not exist
     * @throws NotValidLevelDirectoryException  invalid level directory
     */
    @Operation(
            method = "POST",
            summary = "Manager directory",
            description = "Directory manager based on user rights",
            tags = { "Directory" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data manager directory",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataManagerDirectoryApi.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful directory manager",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseManagerDirectoryApi.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFoundDirectory")
            }
    )
    ResponseManagerDirectoryApi manager(
            final DataManagerDirectoryApi data,
            @Parameter(hidden = true) String accessToken
    ) throws IOException, NotValidLevelDirectoryException;
}
