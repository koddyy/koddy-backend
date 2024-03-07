package com.koddy.server.file.utils.validator

import com.koddy.server.file.domain.model.FileExtension
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

open class ValidPdfFileValidator : ConstraintValidator<ValidPdfFile, String> {
    override fun isValid(
        fileName: String,
        context: ConstraintValidatorContext,
    ): Boolean = FileExtension.isPdf(fileName)
}
