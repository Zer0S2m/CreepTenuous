package com.zer0s2m.creeptenuous.api.documentation.controllers;

import com.zer0s2m.creeptenuous.common.containers.ContainerCategoryFileSystemObject;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataUserCategory;
import com.zer0s2m.creeptenuous.common.data.DataControlFileSystemObjectInCategoryApi;
import com.zer0s2m.creeptenuous.common.data.DataCreateUserCategoryApi;
import com.zer0s2m.creeptenuous.common.data.DataControlAnyObjectApi;
import com.zer0s2m.creeptenuous.common.data.DataEditUserCategoryApi;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundException;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

public interface ControllerApiCategoryUserDoc {

    @Operation(
            method = "GET",
            summary = "GET categories",
            description = "Getting all user categories",
            tags = { "Category" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
            }
    )
    List<ContainerDataUserCategory> getAll(@Parameter(hidden = true) String accessToken);

    /**
     * Creating a category for file objects
     * @param data data to create
     * @param accessToken raw access JWT token
     * @return entity data
     * @throws UserNotFoundException user not exists
     */
    @Operation(
            method = "POST",
            summary = "Create category",
            description = "Creating a custom category for file objects",
            tags = { "Category" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data to created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataCreateUserCategoryApi.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Successful category created",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ContainerDataUserCategory.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found user",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Not found user", value = "{" +
                                                    "\"message\": \"User is not found.\"," +
                                                    "\"status\": 404" +
                                                    "}"
                                            ),
                                    })
                    )
            }
    )
    ContainerDataUserCategory create(
            final DataCreateUserCategoryApi data, @Parameter(hidden = true) String accessToken)
            throws UserNotFoundException;

    /**
     * Update custom category by id
     * @param data data to update
     * @param accessToken raw access JWT token
     * @throws NotFoundException not found user category
     */
    @Operation(
            method = "PUT",
            summary = "Update category",
            description = "Updating a custom category for file objects",
            tags = { "Category" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data to update",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataEditUserCategoryApi.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Successful category updated",
                            content = @Content
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found user",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Not found category", value ="{" +
                                                    "\"message\": \"Not found category\"," +
                                                    "\"status\": 404" +
                                                    "}"
                                            )
                                    })
                    )
            }
    )
    void edit(final DataEditUserCategoryApi data, @Parameter(hidden = true) String accessToken)
            throws NotFoundException;

    /**
     * Delete a custom category by its id
     * @param data data to delete
     * @param accessToken raw access JWT token
     * @throws NotFoundException not found user category
     */
    @Operation(
            method = "DELETE",
            summary = "Delete category",
            description = "Delete a custom category by its id",
            tags = { "Category" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data to created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataControlAnyObjectApi.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successful category deleted",
                            content = @Content
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Not found category", value ="{" +
                                                    "\"message\": \"Not found category\"," +
                                                    "\"status\": 404" +
                                                    "}"
                                            )
                                    })
                    )
            }
    )
    void delete(final DataControlAnyObjectApi data, @Parameter(hidden = true) String accessToken)
            throws NotFoundException;

    /**
     * Bind a file object to a custom category
     * @param data binding data
     * @param accessToken raw access JWT token
     * @throws NotFoundException not found category or file system object
     */
    @Operation(
            method = "POST",
            summary = "Set category",
            description = "Bind a file object to a custom category",
            tags = { "Category" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data to set",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataControlFileSystemObjectInCategoryApi.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successful category set",
                            content = @Content
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Not found category", value ="{" +
                                                    "\"message\": \"Not found category\"," +
                                                    "\"status\": 404" +
                                                    "}"
                                            ),
                                            @ExampleObject(name = "Not found system object", value ="{" +
                                                    "\"message\": \"Not found file system object\"," +
                                                    "\"status\": 404" +
                                                    "}"
                                            ),
                                            @ExampleObject(name = "Not found user", value = "{" +
                                                    "\"message\": \"User is not found.\"," +
                                                    "\"status\": 404" +
                                                    "}"
                                            ),
                                    })
                    )
            }
    )
    void setFileSystemObjectInCategory(
            final DataControlFileSystemObjectInCategoryApi data,
            @Parameter(hidden = true) String accessToken) throws NotFoundException;

    /**
     * Link a file object to a custom category
     * @param data data to unbind
     * @param accessToken raw access JWT token
     * @throws NotFoundException not found category or file system object
     */
    @Operation(
            method = "DELETE",
            summary = "Unset category",
            description = "Link a file object to a custom category",
            tags = { "Category" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data to unset",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataControlFileSystemObjectInCategoryApi.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successful category unset",
                            content = @Content
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Not found category", value ="{" +
                                                    "\"message\": \"Not found category\"," +
                                                    "\"status\": 404" +
                                                    "}"
                                            ),
                                            @ExampleObject(name = "Not found system object", value ="{" +
                                                    "\"message\": \"Not found file system object\"," +
                                                    "\"status\": 404" +
                                                    "}"
                                            )
                                    })
                    )
            }
    )
    void unsetFileSystemObjectInCategory(
            final DataControlFileSystemObjectInCategoryApi data,
            @Parameter(hidden = true) String accessToken) throws NotFoundException;

    /**
     * Get all objects of the file category associated with the user category by ID
     * @param categoryId ID custom category
     * @param accessToken raw access JWT token
     * @return linked file category objects to user category
     * @throws NotFoundException not found category
     */
    @Operation(
            method = "GET",
            summary = "File objects associated with a category",
            description = "Get all objects of the file category associated with the user category by ID",
            tags = { "Category" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful getting",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(value = "[" +
                                                    "{" +
                                                    "\"categoryId\": 2," +
                                                    "\"fileSystemObject\": \"78b1bbdb-14a9-4ccc-82b4-4984314b064d\"" +
                                                    "}" +
                                                    "]")
                                    }
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Not found category", value ="{" +
                                                    "\"message\": \"Not found category\"," +
                                                    "\"status\": 404" +
                                                    "}"
                                            )
                                    })
                    )
            }
    )
    List<ContainerCategoryFileSystemObject> getFileSystemObjectInCategoryByCategoryId(
            final Long categoryId, @Parameter(hidden = true) String accessToken)
            throws NotFoundException;

}
