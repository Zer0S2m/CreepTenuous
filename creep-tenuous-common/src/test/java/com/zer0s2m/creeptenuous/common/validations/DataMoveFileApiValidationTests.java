package com.zer0s2m.creeptenuous.common.validations;

import com.zer0s2m.creeptenuous.common.data.DataMoveFileApi;
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
public class DataMoveFileApiValidationTests extends BaseValidationDataApi<DataMoveFileApi> {
    @Test
    public void notValidParents_fail() {
        DataMoveFileApi invalidDataMoveFile = new DataMoveFileApi(
                "file.txt",
                "file.txt",
                null,
                null,
                null,
                null,
                new ArrayList<>(),
                new ArrayList<>()
        );
        setErrorInvalidData(getValidator(), invalidDataMoveFile);
    }

    @Test
    public void notValidParents_toParents() {
        DataMoveFileApi invalidDataMoveFile = new DataMoveFileApi(
                "file.txt",
                "file.txt",
                null,
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                null,
                null
        );
        setErrorInvalidData(getValidator(), invalidDataMoveFile);
    }
}
