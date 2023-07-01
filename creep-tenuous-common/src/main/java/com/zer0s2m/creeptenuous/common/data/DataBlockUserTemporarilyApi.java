package com.zer0s2m.creeptenuous.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record DataBlockUserTemporarilyApi(

        @NotNull(message = "Please provide user login (Not NULL)")
        @NotBlank(message = "Please provide user login")
        String login,

        @Schema(description = "Blocking start date (if the date is not specified, the current one is taken)")
        LocalDateTime fromDate,

        @NotNull(message = "Please enter an expiration date (Not NULL)")
        @Schema(description = "Blocking end date")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime toDate

) {

    @Override
    public LocalDateTime fromDate() {
        if (fromDate == null) {
            LocalDateTime.now();
        }
        return fromDate;
    }

}
