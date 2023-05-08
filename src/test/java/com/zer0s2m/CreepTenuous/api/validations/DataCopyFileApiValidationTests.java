package com.zer0s2m.CreepTenuous.api.validations;

import com.zer0s2m.CreepTenuous.helpers.BaseValidationDataApi;
import com.zer0s2m.CreepTenuous.api.controllers.files.copy.data.DataCopyFile;

import com.zer0s2m.CreepTenuous.helpers.TestTagValidationApi;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagValidationApi
public class DataCopyFileApiValidationTests extends BaseValidationDataApi<DataCopyFile> {
    @Test
    public void notValidParents_fail() {
        DataCopyFile invalidDataCopyFile = new DataCopyFile(
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
        DataCopyFile invalidDataCopyFile = new DataCopyFile(
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