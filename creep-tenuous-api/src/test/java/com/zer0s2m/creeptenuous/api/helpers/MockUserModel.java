package com.zer0s2m.creeptenuous.api.helpers;

public record MockUserModel(
    String login,
    String password,
    String email,
    String name,
    String role
) {}
