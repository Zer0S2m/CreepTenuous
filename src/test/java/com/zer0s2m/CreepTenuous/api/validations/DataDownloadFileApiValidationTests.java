package com.zer0s2m.CreepTenuous.api.validations;

import com.zer0s2m.CreepTenuous.api.controllers.files.download.data.DataDownloadFile;
import com.zer0s2m.CreepTenuous.helpers.BaseValidationDataApi;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class DataDownloadFileApiValidationTests extends BaseValidationDataApi<DataDownloadFile> {
    @Test
    public void notValidNameFile_fail() {
        DataDownloadFile invalidDataDownloadFile = new DataDownloadFile(new ArrayList<>(), "");
        setErrorInvalidData(getValidator(), invalidDataDownloadFile);
    }

    @Test
    public void notValidParents_fail() {
        DataDownloadFile invalidDataDownloadFile = new DataDownloadFile(null, "testFile.txt");
        setErrorInvalidData(getValidator(), invalidDataDownloadFile);
    }
}
