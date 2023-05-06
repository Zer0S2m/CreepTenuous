package com.zer0s2m.CreepTenuous.api.validations;

import com.zer0s2m.CreepTenuous.api.controllers.directory.copy.data.FormCopyDirectoryApi;
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
public class DataCopyDirectoryApiValidationTests extends BaseValidationDataApi<FormCopyDirectoryApi> {
    @Test
    public void notValidParents_fail() {
        FormCopyDirectoryApi invalidDataCopyDirectory = new FormCopyDirectoryApi(
                null, new ArrayList<>(), "testFolder", 1
        );
        setErrorInvalidData(getValidator(), invalidDataCopyDirectory);
    }

    @Test
    public void notValidToParents_fail() {
        FormCopyDirectoryApi invalidDataCopyDirectory = new FormCopyDirectoryApi(
                new ArrayList<>(), null, "testFolder", 1
        );
        setErrorInvalidData(getValidator(), invalidDataCopyDirectory);
    }

    @Test
    public void notValidNameFile_fail() {
        FormCopyDirectoryApi invalidDataCopyDirectory = new FormCopyDirectoryApi(
                new ArrayList<>(), new ArrayList<>(), null, 1
        );
        setErrorInvalidData(getValidator(), invalidDataCopyDirectory);
    }

    @Test
    public void notValidMethod_fail() {
        FormCopyDirectoryApi invalidDataCopyDirectory = new FormCopyDirectoryApi(
                new ArrayList<>(), new ArrayList<>(), "testFolder", null
        );
        setErrorInvalidData(getValidator(), invalidDataCopyDirectory);
    }
}
