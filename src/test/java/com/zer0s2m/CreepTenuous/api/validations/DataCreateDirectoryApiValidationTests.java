package com.zer0s2m.CreepTenuous.api.validations;

import com.zer0s2m.CreepTenuous.api.controllers.directory.create.data.FormCreateDirectoryApi;
import com.zer0s2m.CreepTenuous.helpers.BaseValidationDataApi;

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
public class DataCreateDirectoryApiValidationTests extends BaseValidationDataApi<FormCreateDirectoryApi> {
    @Test
    public void notValidParents_fail() {
        FormCreateDirectoryApi invalidDataCreateDirectory = new FormCreateDirectoryApi(
                null, null, "testFolder"
        );
        setErrorInvalidData(getValidator(), invalidDataCreateDirectory);
    }

    @Test
    public void notValidNameFile_fail() {
        FormCreateDirectoryApi invalidDataCreateDirectory = new FormCreateDirectoryApi(
                new ArrayList<>(), new ArrayList<>(), ""
        );
        setErrorInvalidData(getValidator(), invalidDataCreateDirectory);
    }
}
