package com.zer0s2m.creeptenuous.common.validations;

import com.zer0s2m.creeptenuous.common.data.DataMoveDirectoryApi;
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
public class DataMoveDirectoryApiValidationsTests extends BaseValidationDataApi<DataMoveDirectoryApi> {
    @Test
    public void notValidParents_fail() {
        DataMoveDirectoryApi invalidDataCopyDirectory = new DataMoveDirectoryApi(
                null,
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                "testFolder",
                "testFolder",
                1
        );
        setErrorInvalidData(getValidator(), invalidDataCopyDirectory);
    }

    @Test
    public void notValidToParents_fail() {
        DataMoveDirectoryApi invalidDataCopyDirectory = new DataMoveDirectoryApi(
                new ArrayList<>(),
                new ArrayList<>(),
                null,
                null,
                "testFolder",
                "testFolder",
                1
        );
        setErrorInvalidData(getValidator(), invalidDataCopyDirectory);
    }

    @Test
    public void notValidNameFile_fail() {
        DataMoveDirectoryApi invalidDataCopyDirectory = new DataMoveDirectoryApi(
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                null,
                null,
                1
        );
        setErrorInvalidData(getValidator(), invalidDataCopyDirectory);
    }

    @Test
    public void notValidMethod_fail() {
        DataMoveDirectoryApi invalidDataCopyDirectory = new DataMoveDirectoryApi(
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                "testFolder",
                "testFolder",
                null
        );
        setErrorInvalidData(getValidator(), invalidDataCopyDirectory);
    }
}
