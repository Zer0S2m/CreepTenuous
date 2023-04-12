package com.zer0s2m.CreepTenuous.api.validations;

import com.zer0s2m.CreepTenuous.Helpers.BaseValidationDataApi;
import com.zer0s2m.CreepTenuous.api.controllers.files.delete.data.DataDeleteFile;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class DataDeleteFileApiValidationsTests extends BaseValidationDataApi<DataDeleteFile>
{
    @Test
    public void notValidNameFile_fail() {
        DataDeleteFile invalidDataDeleteFile = new DataDeleteFile("", new ArrayList<>());
        setErrorInvalidData(getValidator(), invalidDataDeleteFile);
    }

    @Test
    public void notValidParents_fail() {
        DataDeleteFile invalidDataDeleteFile = new DataDeleteFile("testFile", null);
        setErrorInvalidData(getValidator(), invalidDataDeleteFile);
    }
}
