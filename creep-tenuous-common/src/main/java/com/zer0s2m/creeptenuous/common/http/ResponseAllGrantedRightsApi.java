package com.zer0s2m.creeptenuous.common.http;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ResponseAllGrantedRightsApi(

        @Schema(description = "Information about all issued rights to all objects")
        List<ResponseGrantedRightsApi> rights

) { }
