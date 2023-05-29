package com.zer0s2m.creeptenuous.api.documentation.config;

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

    @Bean
    public OpenAPI customOpenAPI() {
        Schema schemaForbidden = new Schema<Map<String, Object>>()
                .addProperty("message", new StringSchema().example("Forbidden"))
                .addProperty("statusCode", new IntegerSchema().example(403));

        ApiResponse responseForbidden = new ApiResponse();
        MediaType mediaTypeForbidden = new MediaType();
        mediaTypeForbidden.setSchema(schemaForbidden);
        responseForbidden.setContent(new Content()
                .addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE, mediaTypeForbidden)
        );
        responseForbidden.setDescription("Insufficient rights to perform an operation on a file system object");

        return new OpenAPI()
                .components(new Components()
                        .addSchemas("Forbidden" , schemaForbidden)
                        .addResponses("Forbidden", responseForbidden)
                );
    }
}
