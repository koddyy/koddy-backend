package com.koddy.server.file.presentation

import com.koddy.server.auth.domain.model.Authenticated
import com.koddy.server.file.application.usecase.RegisterPresignedUrlUseCase
import com.koddy.server.file.application.usecase.UploadFileUseCase
import com.koddy.server.file.application.usecase.command.UploadFileCommand
import com.koddy.server.file.domain.model.PresignedUrlDetails
import com.koddy.server.file.presentation.request.GetImagePresignedUrlRequest
import com.koddy.server.file.presentation.request.GetPdfPresignedUrlRequest
import com.koddy.server.file.utils.converter.FileConverter
import com.koddy.server.global.ResponseWrapper
import com.koddy.server.global.annotation.Auth
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@Tag(name = "3. 파일 관련 API")
@RestController
@RequestMapping("/api/files")
class FileManagementApi(
    private val registerPresignedUrlUseCase: RegisterPresignedUrlUseCase,
    private val uploadFileUseCase: UploadFileUseCase,
) {
    @Operation(summary = "이미지 업로드에 대한 Presigned Url 응답 Endpoint")
    @GetMapping("/presigned/image")
    fun getImagePresignedUrl(
        @Auth authenticated: Authenticated,
        @ModelAttribute @Valid request: GetImagePresignedUrlRequest,
    ): ResponseEntity<PresignedUrlDetails> {
        val presignedUrl: PresignedUrlDetails = registerPresignedUrlUseCase.invoke(request.toCommand())
        return ResponseEntity.ok(presignedUrl)
    }

    @Operation(summary = "PDF 파일 업로드에 대한 Presigned Url 응답 Endpoint")
    @GetMapping("/presigned/pdf")
    fun getPdfPresignedUrl(
        @Auth authenticated: Authenticated,
        @ModelAttribute @Valid request: GetPdfPresignedUrlRequest,
    ): ResponseEntity<PresignedUrlDetails> {
        val presignedUrl: PresignedUrlDetails = registerPresignedUrlUseCase.invoke(request.toCommand())
        return ResponseEntity.ok(presignedUrl)
    }

    @Operation(summary = "파일 업로드 Endpoint - Deprecated")
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun upload(
        @Auth authenticated: Authenticated,
        @RequestPart file: MultipartFile,
    ): ResponseEntity<ResponseWrapper<String>> {
        val uploadUrl: String = uploadFileUseCase.invoke(UploadFileCommand(FileConverter.convertFile(file)))
        return ResponseEntity.ok(ResponseWrapper(uploadUrl))
    }
}
