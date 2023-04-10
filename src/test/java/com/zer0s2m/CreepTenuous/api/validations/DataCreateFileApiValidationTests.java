package com.zer0s2m.CreepTenuous.api.validations;

import com.zer0s2m.CreepTenuous.api.controllers.ControllerApiCreateFileTests;
import com.zer0s2m.CreepTenuous.api.controllers.files.create.data.DataCreateFile;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class DataCreateFileApiValidationTests {
    Logger logger = LogManager.getLogger(ControllerApiCreateFileTests.class);

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

    protected Validator getValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        return factory.getValidator();
    }

    protected void setErrorInvalidData(Validator validator, DataCreateFile invalidData) {
        Set<ConstraintViolation<DataCreateFile>> violations = validator.validate(invalidData);
        for (ConstraintViolation<DataCreateFile> violation : violations) {
            String error = violation.getMessage();
            assertThat(error).isNotEmpty();
            logger.info("Error in validation (tests) - " + error);
        }
    }
}
