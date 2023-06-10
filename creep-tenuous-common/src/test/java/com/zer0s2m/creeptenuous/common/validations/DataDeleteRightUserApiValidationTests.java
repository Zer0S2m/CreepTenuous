package com.zer0s2m.creeptenuous.common.validations;

import com.zer0s2m.creeptenuous.common.data.DataDeleteRightUserApi;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagValidationApi;
import com.zer0s2m.creeptenuous.starter.test.helpers.BaseValidationDataApi;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagValidationApi
public class DataDeleteRightUserApiValidationTests extends BaseValidationDataApi<DataDeleteRightUserApi> {

    @Test
    public void invalidSystemName_fail() {
        DataDeleteRightUserApi invalidDataDeleteRight = new DataDeleteRightUserApi(
                null,
                "login",
                "MOVE"
        );
        setErrorInvalidData(getValidator(), invalidDataDeleteRight);
    }

    @Test
    public void invalidSLoginUser_fail() {
        DataDeleteRightUserApi invalidDataDeleteRight = new DataDeleteRightUserApi(
                "systemName",
                null,
                "MOVE"
        );
        setErrorInvalidData(getValidator(), invalidDataDeleteRight);
    }

    @Test
    public void invalidOperation_fail() {
        DataDeleteRightUserApi invalidDataDeleteRight = new DataDeleteRightUserApi(
                "systemName",
                "login",
                "invalid_type_format_enum"
        );
        setErrorInvalidData(getValidator(), invalidDataDeleteRight);
    }

    @Test
    public void isNullOperation_fail() {
        DataDeleteRightUserApi invalidDataDeleteRight = new DataDeleteRightUserApi(
                "systemName",
                "login",
                null
        );
        setErrorInvalidData(getValidator(), invalidDataDeleteRight);
    }
    
}
