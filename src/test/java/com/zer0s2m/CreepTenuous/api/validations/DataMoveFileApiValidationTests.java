package com.zer0s2m.CreepTenuous.api.validations;

import com.zer0s2m.CreepTenuous.helpers.BaseValidationDataApi;
import com.zer0s2m.CreepTenuous.api.controllers.files.move.data.DataMoveFile;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
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
