package com.zer0s2m.creeptenuous.common.validations;

import com.zer0s2m.creeptenuous.common.data.DataCreateRightUserApi;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagValidationApi;
import com.zer0s2m.creeptenuous.starter.test.helpers.BaseValidationDataApi;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagValidationApi
public class DataCreateRightUserApiValidationTests extends BaseValidationDataApi<DataCreateRightUserApi> {

    @Test
    public void invalidSystemName_fail() {
        DataCreateRightUserApi invalidDataCreateRight = new DataCreateRightUserApi(
                null,
                "login",
                List.of("MOVE")
        );
        setErrorInvalidData(getValidator(), invalidDataCreateRight);
    }

    @Test
    public void invalidSLoginUser_fail() {
        DataCreateRightUserApi invalidDataCreateRight = new DataCreateRightUserApi(
                "systemName",
                null,
                List.of("MOVE")
        );
        setErrorInvalidData(getValidator(), invalidDataCreateRight);
    }

    @Test
    public void invalidOperation_fail() {
        DataCreateRightUserApi invalidDataCreateRight = new DataCreateRightUserApi(
                "systemName",
                "login",
                List.of("invalid_type_format_enum")
        );
        setErrorInvalidData(getValidator(), invalidDataCreateRight);
    }

    @Test
    public void isNullOperation_fail() {
        DataCreateRightUserApi invalidDataCreateRight = new DataCreateRightUserApi(
                "systemName",
                "login",
                null
        );
        setErrorInvalidData(getValidator(), invalidDataCreateRight);
    }

}
