package com.zer0s2m.creeptenuous.common.validations;

import com.zer0s2m.creeptenuous.common.data.DataDownloadDirectoryApi;
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
public class DataDownloadDirectoryApiValidationTests extends BaseValidationDataApi<DataDownloadDirectoryApi>
{
    @Test
    public void notValidNameFile_fail() {
        DataDownloadDirectoryApi invalidDataDownloadDirectory = new DataDownloadDirectoryApi(
                new ArrayList<>(),
                new ArrayList<>(),
                null,
                null
        );
        setErrorInvalidData(getValidator(), invalidDataDownloadDirectory);
    }

    @Test
    public void notValidParents_fail() {
        DataDownloadDirectoryApi invalidDataDownloadDirectory = new DataDownloadDirectoryApi(
                null,
                null,
                "testFolder",
                "testFolder"
        );
        setErrorInvalidData(getValidator(), invalidDataDownloadDirectory);
    }
}
