package com.zer0s2m.CreepTenuous.api.validations;

import com.zer0s2m.CreepTenuous.api.controllers.directory.download.data.DataDownloadDirectory;
import com.zer0s2m.CreepTenuous.helpers.BaseValidationDataApi;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class DataDownloadDirectoryApiValidationTests extends BaseValidationDataApi<DataDownloadDirectory>
{
    @Test
    public void notValidNameFile_fail() {
        DataDownloadDirectory invalidDataDownloadDirectory = new DataDownloadDirectory(new ArrayList<>(), null);
        setErrorInvalidData(getValidator(), invalidDataDownloadDirectory);
    }

    @Test
    public void notValidParents_fail() {
        DataDownloadDirectory invalidDataDownloadDirectory = new DataDownloadDirectory(null, "testFolder");
        setErrorInvalidData(getValidator(), invalidDataDownloadDirectory);
    }
}
