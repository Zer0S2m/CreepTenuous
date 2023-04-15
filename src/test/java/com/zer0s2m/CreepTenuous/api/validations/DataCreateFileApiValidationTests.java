package com.zer0s2m.CreepTenuous.api.validations;

import com.zer0s2m.CreepTenuous.helpers.BaseValidationDataApi;
import com.zer0s2m.CreepTenuous.api.controllers.files.create.data.DataCreateFile;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class DataCreateFileApiValidationTests extends BaseValidationDataApi<DataCreateFile> {
    @Test
    public void notValidTypeFile_fail() {
        DataCreateFile invalidDataCreateFile = new DataCreateFile(null, "testFile", new ArrayList<>());
        setErrorInvalidData(getValidator(), invalidDataCreateFile);
    }

    @Test
    public void notValidNameFile_fail() {
        DataCreateFile invalidDataCreateFile = new DataCreateFile(1, "", new ArrayList<>());
        setErrorInvalidData(getValidator(), invalidDataCreateFile);
    }

    @Test
    public void notValidParents_fail() {
        DataCreateFile invalidDataCreateFile = new DataCreateFile(1, "testFile", null);
        setErrorInvalidData(getValidator(), invalidDataCreateFile);
    }
}
