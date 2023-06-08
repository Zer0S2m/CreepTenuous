package com.zer0s2m.creeptenuous.common.validations;

import com.zer0s2m.creeptenuous.common.data.DataCreateRightUserApi;
import com.zer0s2m.creeptenuous.common.helpers.BaseValidationDataApi;
import com.zer0s2m.creeptenuous.common.helpers.TestTagValidationApi;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagValidationApi
public class DataCreateRightUserApiValidationTests extends BaseValidationDataApi<DataCreateRightUserApi> {

    @Test
    public void invalidSystemName_fail() {
        DataCreateRightUserApi invalidDataCreateRight = new DataCreateRightUserApi(
                null,
                "login",
                "MOVE"
        );
        setErrorInvalidData(getValidator(), invalidDataCreateRight);
    }

    @Test
    public void invalidSLoginUser_fail() {
        DataCreateRightUserApi invalidDataCreateRight = new DataCreateRightUserApi(
                "systemName",
                null,
                "MOVE"
        );
        setErrorInvalidData(getValidator(), invalidDataCreateRight);
    }

    @Test
    public void invalidOperation_fail() {
        DataCreateRightUserApi invalidDataCreateRight = new DataCreateRightUserApi(
                "systemName",
                "login",
                "invalid_type_format_enum"
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
