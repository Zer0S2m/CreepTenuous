package com.zer0s2m.CreepTenuous.api.validations;

import com.zer0s2m.CreepTenuous.api.controllers.directory.move.data.FormMoveDirectoryApi;
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
public class DataMoveDirectoryApiValidationsTests extends BaseValidationDataApi<FormMoveDirectoryApi> {
    @Test
    public void notValidParents_fail() {
        FormMoveDirectoryApi invalidDataCopyDirectory = new FormMoveDirectoryApi(
                null,
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                "testFolder",
                "testFolder",
                1
        );
        setErrorInvalidData(getValidator(), invalidDataCopyDirectory);
    }

    @Test
    public void notValidToParents_fail() {
        FormMoveDirectoryApi invalidDataCopyDirectory = new FormMoveDirectoryApi(
                new ArrayList<>(),
                new ArrayList<>(),
                null,
                null,
                "testFolder",
                "testFolder",
                1
        );
        setErrorInvalidData(getValidator(), invalidDataCopyDirectory);
    }

    @Test
    public void notValidNameFile_fail() {
        FormMoveDirectoryApi invalidDataCopyDirectory = new FormMoveDirectoryApi(
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                null,
                null,
                1
        );
        setErrorInvalidData(getValidator(), invalidDataCopyDirectory);
    }

    @Test
    public void notValidMethod_fail() {
        FormMoveDirectoryApi invalidDataCopyDirectory = new FormMoveDirectoryApi(
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                "testFolder",
                "testFolder",
                null
        );
        setErrorInvalidData(getValidator(), invalidDataCopyDirectory);
    }
}
