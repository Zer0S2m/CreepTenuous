package com.zer0s2m.creeptenuous.common.validations;

import com.zer0s2m.creeptenuous.common.data.DataCreateFileApi;
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
public class DataCreateFileApiValidationTests extends BaseValidationDataApi<DataCreateFileApi> {
    @Test
    public void notValidTypeFile_fail() {
        DataCreateFileApi invalidDataCreateFile = new DataCreateFileApi(
                null,
                "testFile",
                new ArrayList<>(),
                new ArrayList<>()
        );
        setErrorInvalidData(getValidator(), invalidDataCreateFile);
    }

    @Test
    public void notValidNameFile_fail() {
        DataCreateFileApi invalidDataCreateFile = new DataCreateFileApi(
                1,
                "",
                new ArrayList<>(),
                new ArrayList<>()
        );
        setErrorInvalidData(getValidator(), invalidDataCreateFile);
    }

    @Test
    public void notValidParents_fail() {
        DataCreateFileApi invalidDataCreateFile = new DataCreateFileApi(1, "testFile", null, null);
        setErrorInvalidData(getValidator(), invalidDataCreateFile);
    }

}
