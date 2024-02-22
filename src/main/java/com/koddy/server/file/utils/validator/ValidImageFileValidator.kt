package com.koddy.server.file.utils.validator

import com.koddy.server.file.domain.model.FileExtension.Companion.isImage
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

open class ValidImageFileValidator : ConstraintValidator<ValidImageFile, String> {
    override fun isValid(
        fileName: String,
        context: ConstraintValidatorContext,
    ): Boolean = isImage(fileName)
}
