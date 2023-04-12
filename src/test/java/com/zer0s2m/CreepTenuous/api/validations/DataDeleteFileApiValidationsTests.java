package com.zer0s2m.CreepTenuous.api.validations;

import com.zer0s2m.CreepTenuous.api.controllers.ControllerApiCreateFileTests;
import com.zer0s2m.CreepTenuous.api.controllers.files.delete.data.DataDeleteFile;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class DataDeleteFileApiValidationsTests {
    Logger logger = LogManager.getLogger(ControllerApiCreateFileTests.class);

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

    protected Validator getValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        return factory.getValidator();
    }

    protected void setErrorInvalidData(Validator validator, DataDeleteFile invalidData) {
        Set<ConstraintViolation<DataDeleteFile>> violations = validator.validate(invalidData);
        for (ConstraintViolation<DataDeleteFile> violation : violations) {
            String error = violation.getMessage();
            assertThat(error).isNotEmpty();
            logger.info("Error in validation (tests) - " + error);
        }
    }
}
