package com.zer0s2m.creeptenuous.common.validations;

import com.zer0s2m.creeptenuous.common.data.DataDeleteFileApi;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagValidationApi;
import com.zer0s2m.creeptenuous.starter.test.helpers.BaseValidationDataApi;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagValidationApi
public class DataDeleteFileApiValidationsTests extends BaseValidationDataApi<DataDeleteFileApi>
{
    @Test
    public void notValidNameFile_fail() {
        DataDeleteFileApi invalidDataDeleteFile = new DataDeleteFileApi(
                "",
                "",
                new ArrayList<>(),
                new ArrayList<>()
        );
        setErrorInvalidData(getValidator(), invalidDataDeleteFile);
    }

    @Test
    public void notValidParents_fail() {
        DataDeleteFileApi invalidDataDeleteFile = new DataDeleteFileApi(
                "testFile",
                "testFile",
                null,
                null
        );
        setErrorInvalidData(getValidator(), invalidDataDeleteFile);
    }

}
