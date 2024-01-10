package com.koddy.server.file.utils.validator;

import com.koddy.server.file.domain.model.FileExtension;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidImageFileValidator implements ConstraintValidator<ValidImageFile, String> {
    @Override
    public boolean isValid(final String fileName, final ConstraintValidatorContext context) {
        return FileExtension.isImage(fileName);
    }
}
