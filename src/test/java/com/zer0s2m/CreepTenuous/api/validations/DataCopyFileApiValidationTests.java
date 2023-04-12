package com.zer0s2m.CreepTenuous.api.validations;

import com.zer0s2m.CreepTenuous.Helpers.BaseValidationDataApi;
import com.zer0s2m.CreepTenuous.api.controllers.files.copy.data.DataCopyFile;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class DataCopyFileApiValidationTests extends BaseValidationDataApi<DataCopyFile> {
    @Test
    public void notValidParents_fail() {
        DataCopyFile invalidDataCopyFile = new DataCopyFile(
                "file.txt",
                null,
                null,
                new ArrayList<>()
        );
        setErrorInvalidData(getValidator(), invalidDataCopyFile);
    }

    @Test
    public void notValidParents_toParents() {
        DataCopyFile invalidDataCopyFile = new DataCopyFile(
                "file.txt",
                null,
                new ArrayList<>(),
                null
        );
        setErrorInvalidData(getValidator(), invalidDataCopyFile);
    }
}