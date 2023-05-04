package com.zer0s2m.CreepTenuous.api.validations;

import com.zer0s2m.CreepTenuous.helpers.BaseValidationDataApi;
import com.zer0s2m.CreepTenuous.api.controllers.files.delete.data.DataDeleteFile;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class DataDeleteFileApiValidationsTests extends BaseValidationDataApi<DataDeleteFile>
{
    @Test
    public void notValidNameFile_fail() {
        DataDeleteFile invalidDataDeleteFile = new DataDeleteFile(
                "",
                "",
                new ArrayList<>(),
                new ArrayList<>()
        );
        setErrorInvalidData(getValidator(), invalidDataDeleteFile);
    }

    @Test
    public void notValidParents_fail() {
        DataDeleteFile invalidDataDeleteFile = new DataDeleteFile(
                "testFile",
                "testFile",
                null,
                null
        );
        setErrorInvalidData(getValidator(), invalidDataDeleteFile);
    }
}
