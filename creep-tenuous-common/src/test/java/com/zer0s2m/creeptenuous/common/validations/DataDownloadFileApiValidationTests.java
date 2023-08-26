package com.zer0s2m.creeptenuous.common.validations;

import com.zer0s2m.creeptenuous.common.data.DataDownloadFileApi;
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
public class DataDownloadFileApiValidationTests extends BaseValidationDataApi<DataDownloadFileApi> {
    @Test
    public void notValidNameFile_fail() {
        DataDownloadFileApi invalidDataDownloadFile = new DataDownloadFileApi(
                new ArrayList<>(),
                new ArrayList<>(),
                "",
                ""
        );
        setErrorInvalidData(getValidator(), invalidDataDownloadFile);
    }

    @Test
    public void notValidParents_fail() {
        DataDownloadFileApi invalidDataDownloadFile = new DataDownloadFileApi(
                null,
                null,
                "testFile.txt",
                "testFile.txt"
        );
        setErrorInvalidData(getValidator(), invalidDataDownloadFile);
    }

}
