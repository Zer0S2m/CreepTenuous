package com.zer0s2m.creeptenuous.common.validations;

import com.zer0s2m.creeptenuous.common.data.DataCopyFileApi;
import com.zer0s2m.creeptenuous.common.helpers.BaseValidationDataApi;
import com.zer0s2m.creeptenuous.common.helpers.TestTagValidationApi;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagValidationApi
public class DataCopyFileApiValidationTests extends BaseValidationDataApi<DataCopyFileApi> {
    @Test
    public void notValidParents_fail() {
        DataCopyFileApi invalidDataCopyFile = new DataCopyFileApi(
                "file.txt",
                "file.txt",
                null,
                null,
                null,
                null,
                new ArrayList<>(),
                new ArrayList<>()
        );
        setErrorInvalidData(getValidator(), invalidDataCopyFile);
    }

    @Test
    public void notValidParents_toParents() {
        DataCopyFileApi invalidDataCopyFile = new DataCopyFileApi(
                "file.txt",
                "file.txt",
                null,
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                null,
                null
        );
        setErrorInvalidData(getValidator(), invalidDataCopyFile);
    }
}