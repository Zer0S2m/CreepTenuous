package com.zer0s2m.creeptenuous.common.validations;

import com.zer0s2m.creeptenuous.common.data.DataDeleteRightUserApi;
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
public class DataDeleteRightUserApiValidationTests extends BaseValidationDataApi<DataDeleteRightUserApi> {

    @Test
    public void invalidSystemName_fail() {
        DataDeleteRightUserApi invalidDataDeleteRight = new DataDeleteRightUserApi(
                null,
                "login",
                List.of("MOVE")
        );
        setErrorInvalidData(getValidator(), invalidDataDeleteRight);
    }

    @Test
    public void invalidSLoginUser_fail() {
        DataDeleteRightUserApi invalidDataDeleteRight = new DataDeleteRightUserApi(
                "systemName",
                null,
                List.of("MOVE")
        );
        setErrorInvalidData(getValidator(), invalidDataDeleteRight);
    }

    @Test
    public void invalidOperation_fail() {
        DataDeleteRightUserApi invalidDataDeleteRight = new DataDeleteRightUserApi(
                "systemName",
                "login",
                List.of("invalid_type_format_enum")
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
