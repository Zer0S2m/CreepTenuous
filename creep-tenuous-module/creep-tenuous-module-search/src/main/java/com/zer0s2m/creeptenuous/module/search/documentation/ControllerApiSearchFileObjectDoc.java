package com.zer0s2m.creeptenuous.module.search.documentation;

import com.zer0s2m.creeptenuous.module.search.ContainerInfoSearchFileObject;
import com.zer0s2m.creeptenuous.module.search.DataSearchFileObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

public interface ControllerApiSearchFileObjectDoc {

    /**
     * Search for file objects using the following criteria:
     * <ul>
     *     <li>File object type.</li>
     *     <li>Directory nesting level.</li>
     *     <li>Part of the real name of the file object.</li>
     * </ul>
     *
     * @param data        Search data.
     * @param accessToken Raw JWT access token.
     * @return File objects found.
     */
    @Operation(
            method = "POST",
            summary = "Search file object",
            description = "Search for file objects using the following criteria: 1) File object type; " +
                    "2) Directory nesting level; " +
                    "3) Part of the real name of the file object.",
            tags = {"Module-Search"},
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Search data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataSearchFileObject.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful search",
                            content = @Content(
                                    array = @ArraySchema(
                                            schema = @Schema(
                                                    implementation = ContainerInfoSearchFileObject.class
                                            )
                                    ),
                                    mediaType = "application/json"
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            }
    )
    List<ContainerInfoSearchFileObject> search(
            DataSearchFileObject data, @Parameter(hidden = true) String accessToken);

}
