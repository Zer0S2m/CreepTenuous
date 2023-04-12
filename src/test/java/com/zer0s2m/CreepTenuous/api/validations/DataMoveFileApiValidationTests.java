package com.zer0s2m.CreepTenuous.api.validations;

import com.zer0s2m.CreepTenuous.Helpers.BaseValidationDataApi;
import com.zer0s2m.CreepTenuous.api.controllers.files.move.data.DataMoveFile;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class DataMoveFileApiValidationTests extends BaseValidationDataApi<DataMoveFile> {
    @Test
    public void notValidParents_fail() {
        DataMoveFile invalidDataMoveFile = new DataMoveFile(
                "file.txt",
                null,
                null,
                new ArrayList<>()
        );
        setErrorInvalidData(getValidator(), invalidDataMoveFile);
    }

    @Test
    public void notValidParents_toParents() {
        DataMoveFile invalidDataMoveFile = new DataMoveFile(
                "file.txt",
                null,
                new ArrayList<>(),
                null
        );
        setErrorInvalidData(getValidator(), invalidDataMoveFile);
    }
}
