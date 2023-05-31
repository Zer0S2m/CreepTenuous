package com.zer0s2m.creeptenuous.api.documentation.controllers;

import com.zer0s2m.creeptenuous.common.data.DataDownloadFileApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface ControllerApiDownloadFileDoc {

    /**
     * Download file
     * @param data file download data
     * @param accessToken raw JWT access token
     * @return file
     * @throws IOException if an I/O error occurs or the parent directory does not exist
     */
    @Operation(
            method = "POST",
            summary = "Download file",
            description = "file a directory - output format: ",
            tags = { "File" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data to download a directory",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataDownloadFileApi.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful file download",
                            content = @Content(
                                    schema = @Schema(implementation = Resource.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFoundDirectory")
            }
    )
    ResponseEntity<Resource> download(
            final DataDownloadFileApi data,
            @Parameter(hidden = true) String accessToken
    ) throws IOException;
}
