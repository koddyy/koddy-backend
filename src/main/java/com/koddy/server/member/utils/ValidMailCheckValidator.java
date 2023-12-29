package com.koddy.server.member.utils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidMailCheckValidator implements ConstraintValidator<ValidMailCheck, Boolean> {
    @Override
    public boolean isValid(final Boolean value, final ConstraintValidatorContext context) {
        return value;
    }
}
