package com.zer0s2m.creeptenuous.common.validations;

import com.zer0s2m.creeptenuous.common.data.DataDeleteDirectoryApi;
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
public class DataDeleteDirectoryApiValidationTests extends BaseValidationDataApi<DataDeleteDirectoryApi> {
    @Test
    public void notValidParents_fail() {
        DataDeleteDirectoryApi invalidDataDeleteDirectory = new DataDeleteDirectoryApi(
                null, null, "testFolder", "testFolder"
        );
        setErrorInvalidData(getValidator(), invalidDataDeleteDirectory);
    }

    @Test
    public void notValidNameFile_fail() {
        DataDeleteDirectoryApi invalidDataDeleteDirectory = new DataDeleteDirectoryApi(
                new ArrayList<>(), new ArrayList<>(),  "", ""
        );
        setErrorInvalidData(getValidator(), invalidDataDeleteDirectory);
    }
}
