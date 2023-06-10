package com.zer0s2m.creeptenuous.api.documentation.config;

import com.zer0s2m.creeptenuous.common.enums.Directory;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@OpenAPIDefinition
public class DocsConfiguration {

    Schema schemaForbidden;

    Schema schemaNotFoundDirectory;

    Schema schemaNotFoundFile;

    Schema schemaNotFound;

    Schema schemaNotFoundUser;

    Schema schemaInvalidPassword;

    Schema schemaInvalidJwtRefreshToken;

    Schema schemaUnauthorized;

    ApiResponse responseForbidden;

    ApiResponse responseNotFoundDirectory;

    ApiResponse responseNotFoundFile;

    ApiResponse responseNotFound;

    @Bean
    public OpenAPI customOpenAPI() {
        this.buildSchemas();
        this.buildResponses();

        return new OpenAPI()
                .components(new Components()
                        .addSchemas("Forbidden" , this.schemaForbidden)
                        .addSchemas("NotFoundDirectory" , this.schemaNotFoundDirectory)
                        .addSchemas("NotFoundFile" , this.schemaNotFoundFile)
                        .addSchemas("NotFoundFileObjectSystem" , this.schemaNotFound)
                        .addSchemas("NotFoundUser" , this.schemaNotFoundUser)
                        .addSchemas("UserInvalidPassword" , this.schemaInvalidPassword)
                        .addSchemas("InvalidJwtRefreshToken" , this.schemaInvalidJwtRefreshToken)
                        .addSchemas("Unauthorized" , this.schemaUnauthorized)
                        .addResponses("Forbidden", this.responseForbidden)
                        .addResponses("NotFoundDirectory", this.responseNotFoundDirectory)
                        .addResponses("NotFoundFile", this.responseNotFoundFile)
                        .addResponses("NotFoundFileObjectSystem", this.responseNotFound)
                );
    }

    private void buildSchemas() {
        this.schemaForbidden = new Schema<Map<String, Object>>()
                .addProperty("message", new StringSchema().example("Forbidden"))
                .addProperty("status", new IntegerSchema().example(403));
        this.schemaForbidden.setTitle("Forbidden");

        this.schemaNotFoundDirectory = new Schema<Map<String, Object>>()
                .addProperty("message", new StringSchema().example(Directory.NOT_FOUND_DIRECTORY.get()))
                .addProperty("status", new IntegerSchema().example(404));
        this.schemaNotFoundDirectory.setTitle("NotFoundDirectory");

        this.schemaNotFoundFile = new Schema<Map<String, Object>>()
                .addProperty("message", new StringSchema().example("/path/not/found/file"))
                .addProperty("status", new IntegerSchema().example(404));
        this.schemaNotFoundFile.setTitle("NotFoundFile");

        this.schemaNotFound = new Schema<Map<String, Object>>()
                .addProperty("message", new StringSchema().example("File system object not found"))
                .addProperty("status", new IntegerSchema().example(404))
                .addOneOfItem(this.schemaNotFoundDirectory)
                .addOneOfItem(this.schemaNotFoundFile);
        this.schemaNotFound.setTitle("NotFoundFileObjectSystem");

        this.schemaNotFoundUser = new Schema<Map<String, Object>>()
                .addProperty("message", new StringSchema().example("User is not found."))
                .addProperty("status", new IntegerSchema().example(401));
        this.schemaNotFoundUser.setTitle("NotFoundUser");

        this.schemaInvalidPassword = new Schema<Map<String, Object>>()
                .addProperty("message", new StringSchema().example("Incorrect password."))
                .addProperty("status", new IntegerSchema().example(401));
        this.schemaInvalidPassword.setTitle("UserInvalidPassword");

        this.schemaInvalidJwtRefreshToken = new Schema<Map<String, Object>>()
                .addProperty("message", new StringSchema().example("Invalid refresh token"))
                .addProperty("status", new IntegerSchema().example(401));
        this.schemaInvalidJwtRefreshToken.setTitle("InvalidJwtRefreshToken");

        this.schemaUnauthorized = new Schema<Map<String, Object>>()
                .addProperty("message", new StringSchema().example("Unauthorized"))
                .addProperty("status", new IntegerSchema().example(401))
                .addOneOfItem(this.schemaInvalidJwtRefreshToken)
                .addOneOfItem(this.schemaInvalidPassword)
                .addOneOfItem(this.schemaNotFoundUser);
        this.schemaUnauthorized.setTitle("Unauthorized");
    }

    private void buildResponses() {
        responseForbidden = new ApiResponse();
        MediaType mediaTypeForbidden = new MediaType();
        mediaTypeForbidden.setSchema(this.schemaForbidden);
        responseForbidden.setContent(new Content()
                .addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE, mediaTypeForbidden)
        );
        responseForbidden.setDescription("Insufficient rights to perform an operation on a file system object");

        responseNotFoundDirectory = new ApiResponse();
        MediaType mediaTypeNotFoundDirectory = new MediaType();
        mediaTypeNotFoundDirectory.setSchema(this.schemaNotFoundDirectory);
        responseNotFoundDirectory.setContent(new Content()
                .addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE, mediaTypeNotFoundDirectory)
        );
        responseNotFoundDirectory.setDescription("Directory does not exist");

        responseNotFoundFile = new ApiResponse();
        MediaType mediaTypeNotFoundFile = new MediaType();
        mediaTypeNotFoundFile.setSchema(this.schemaNotFoundFile);
        responseNotFoundFile.setContent(new Content()
                .addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE, mediaTypeNotFoundFile)
        );
        responseNotFoundFile.setDescription("File does not exist");

        responseNotFound = new ApiResponse();
        MediaType mediaTypeNotFound = new MediaType();
        mediaTypeNotFound.setSchema(this.schemaNotFound);
        responseNotFound.setContent(new Content()
                .addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE, mediaTypeNotFound)
        );
        responseNotFound.setDescription("File system object not found");
    }
}
