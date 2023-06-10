package com.zer0s2m.creeptenuous.starter.test.mock;

import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagControllerApi;

/**
 * A collection to store user data. Commonly used in controllers {@link TestTagControllerApi}
 * @param login user login
 * @param password user password
 * @param email user email
 * @param name user name
 * @param role user role
 */
public record MockUserModel(
    String login,
    String password,
    String email,
    String name,
    String role
) {}
