package com.zer0s2m.creeptenuous.integration.implants.doc;

import com.zer0s2m.creeptenuous.integration.implants.http.ResponseServerUnavailableApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

/**
 * Interface for implementing a controller responsible for integrating a third-party
 * service for clearing file storage.
 */
public interface ControllerApiIntegrationDoc {

    /**
     * Call the file storage cleanup method from a third-party service
     */
    @Operation(
            method = "GET",
            summary = "Run clean storage",
            description = "Call the file storage cleanup method from a third-party service",
            tags = { "Integration" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "202",
                            description = "Successful run clean",
                            content = @Content
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/ForbiddenServers"),
                    @ApiResponse(
                            responseCode = "503",
                            description = "The server is not ready to handle the request",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ResponseServerUnavailableApi.class
                                    )
                            )
                    ),
            }
    )
    void run();

}
