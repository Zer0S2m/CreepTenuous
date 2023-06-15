package com.zer0s2m.creeptenuous.api.documentation.controllers;

import com.zer0s2m.creeptenuous.common.data.DataRenameFileSystemObjectApi;
import com.zer0s2m.creeptenuous.common.http.ResponseRenameFileSystemObjectDoc;
import com.zer0s2m.creeptenuous.common.exceptions.NoExistsFileSystemObjectRedisException;
import com.zer0s2m.creeptenuous.common.exceptions.NoRightsRedisException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

public interface ControllerApiRenameFileSystemObjectDoc {

    /**
     * Rename file system object
     *
     * @param data        data to rename
     * @param accessToken raw JWT access token
     * @return new name
     * @throws NoExistsFileSystemObjectRedisException the file system object was not found in the database.
     * @throws NoRightsRedisException When the user has no execution right
     */
    @Operation(
            method = "PUT",
            summary = "Renaming file system object",
            description = "Renaming file system object and assigning rights to a user",
            tags = { "Common" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data to rename file system object",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataRenameFileSystemObjectApi.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful file system object renaming",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseRenameFileSystemObjectDoc.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFoundFileObjectSystem")
            }
    )
    ResponseRenameFileSystemObjectDoc rename(
            final DataRenameFileSystemObjectApi data, @Parameter(hidden = true) String accessToken) throws NoExistsFileSystemObjectRedisException;

}
