package com.zer0s2m.CreepTenuous.api.validations;

import com.zer0s2m.CreepTenuous.api.controllers.directory.delete.data.FormDeleteDirectoryApi;
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
public class DataDeleteDirectoryApiValidationTests extends BaseValidationDataApi<FormDeleteDirectoryApi> {
    @Test
    public void notValidParents_fail() {
        FormDeleteDirectoryApi invalidDataDeleteDirectory = new FormDeleteDirectoryApi(
                null, null, "testFolder", "testFolder"
        );
        setErrorInvalidData(getValidator(), invalidDataDeleteDirectory);
    }

    @Test
    public void notValidNameFile_fail() {
        FormDeleteDirectoryApi invalidDataDeleteDirectory = new FormDeleteDirectoryApi(
                new ArrayList<>(), new ArrayList<>(),  "", ""
        );
        setErrorInvalidData(getValidator(), invalidDataDeleteDirectory);
    }
}
