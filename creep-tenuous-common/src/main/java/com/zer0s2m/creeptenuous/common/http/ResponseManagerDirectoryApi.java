package com.zer0s2m.creeptenuous.common.http;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ResponseManagerDirectoryApi(

        @Schema(description = "Parts of system paths (directories) - real names")
        List<String> parents,

        @Schema(description = "Parts of system paths (directories) - source")
        List<String> systemParents,

        @Schema(description = "Nesting level")
        Integer level,

        @Schema(
                description = "Information array of file objects",
                example = "[" +
                        "{" +
                        "\"realName\": \"string\"," +
                        "\"systemName\": \"string\"," +
                        "\"isFile\": false," +
                        "\"isDirectory\": true," +
                        "\"color\": \"#fff\"," +
                        "\"colorId\": 1," +
                        "\"categoryId\": 1" +
                        "}" +
                        "]"
        )
        List<Object> objects

) { }
