package com.zer0s2m.creeptenuous.common.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DataDeleteUserApi(

        @NotNull(message = "Please provide user login (Not NULL)")
        @NotBlank(message = "Please provide user login")
        String login

) { }
