package com.zer0s2m.creeptenuous.common.http;

public record ResponseUserApi(
        String login,

        String email,

        String name,

        String role
) { }
