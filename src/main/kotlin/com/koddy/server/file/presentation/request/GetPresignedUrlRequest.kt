package com.koddy.server.file.presentation.request

import com.koddy.server.file.application.usecase.command.RegisterPresignedUrlCommand
import com.koddy.server.file.domain.model.PresignedFileData
import com.koddy.server.file.utils.validator.ValidImageFile
import com.koddy.server.file.utils.validator.ValidPdfFile
import jakarta.validation.constraints.NotBlank

data class GetImagePresignedUrlRequest(
    @field:NotBlank(message = "파일명은 필수입니다.")
    @field:ValidImageFile
    val fileName: String,
) {
    fun toCommand(): RegisterPresignedUrlCommand = RegisterPresignedUrlCommand(PresignedFileData(fileName))
}

data class GetPdfPresignedUrlRequest(
    @field:NotBlank(message = "파일명은 필수입니다.")
    @field:ValidPdfFile
    val fileName: String,
) {
    fun toCommand(): RegisterPresignedUrlCommand = RegisterPresignedUrlCommand(PresignedFileData(fileName))
}
