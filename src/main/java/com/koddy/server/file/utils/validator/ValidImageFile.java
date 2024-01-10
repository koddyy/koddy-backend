package com.koddy.server.file.utils.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidImageFileValidator.class)
public @interface ValidImageFile {
    String message() default "이미지 파일[JPG, JPEG, PNG]을 업로드해주세요.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
