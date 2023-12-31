package com.koddy.server.member.utils.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidMailAuthenticatedValidator implements ConstraintValidator<ValidMailAuthenticated, Boolean> {
    @Override
    public boolean isValid(final Boolean value, final ConstraintValidatorContext context) {
        if (value == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("이메일 인증 결과는 필수입니다.")
                    .addConstraintViolation();
            return false;
        }
        return value;
    }
}
