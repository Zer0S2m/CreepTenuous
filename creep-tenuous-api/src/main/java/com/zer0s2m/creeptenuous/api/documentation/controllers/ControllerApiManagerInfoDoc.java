package com.zer0s2m.creeptenuous.api.documentation.controllers;

import com.zer0s2m.creeptenuous.common.data.DataManagerInfoApi;
import com.zer0s2m.creeptenuous.common.http.ResponseManagerInfoApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

public interface ControllerApiManagerInfoDoc {

    @Operation(
            method = "POST",
            summary = "Get information about file objects",
            description = "Get information about file objects by system names",
            tags = { "Common" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data system names",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataManagerInfoApi.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful directory manager",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseManagerInfoApi.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
            }
    )
    ResponseManagerInfoApi getInfoFileObjectsBySystemNames(
            final DataManagerInfoApi data, @Parameter(hidden = true) String accessToken);

}
