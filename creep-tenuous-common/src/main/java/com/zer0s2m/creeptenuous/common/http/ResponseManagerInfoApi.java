package com.zer0s2m.creeptenuous.common.http;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ResponseManagerInfoApi(

        @Schema(
                description = "Information array of file objects",
                example = "[" +
                        "{" +
                        "\"realName\": \"string\"," +
                        "\"systemName\": \"string\"," +
                        "\"isFile\": false," +
                        "\"isDirectory\": true," +
                        "\"color\": \"#fff\"," +
                        "\"createdAt\": \"2023-10-14T13:37:46.859674169\"," +
                        "\"colorId\": 1," +
                        "\"categoryId\": 1" +
                        "}" +
                        "]"
        )
        List<Object> objects

) { }
