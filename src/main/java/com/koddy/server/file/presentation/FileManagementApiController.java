package com.koddy.server.file.presentation;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.file.application.usecase.RegisterPresignedUrlUseCase;
import com.koddy.server.file.application.usecase.UploadFileUseCase;
import com.koddy.server.file.application.usecase.command.RegisterPresignedUrlCommand;
import com.koddy.server.file.application.usecase.command.UploadFileCommand;
import com.koddy.server.file.domain.model.PresignedFileData;
import com.koddy.server.file.domain.model.PresignedUrlDetails;
import com.koddy.server.file.presentation.dto.request.GetImagePresignedUrlRequest;
import com.koddy.server.file.presentation.dto.request.GetPdfPresignedUrlRequest;
import com.koddy.server.file.utils.converter.FileConverter;
import com.koddy.server.global.ResponseWrapper;
import com.koddy.server.global.annotation.Auth;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Tag(name = "3. 파일 관련 API")
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileManagementApiController {
    private final RegisterPresignedUrlUseCase registerPresignedUrlUseCase;
    private final UploadFileUseCase uploadFileUseCase;

    @Operation(summary = "이미지 업로드에 대한 Presigned Url 응답 Endpoint")
    @GetMapping("/presigned/image")
    public ResponseEntity<PresignedUrlDetails> getImagePresignedUrl(
            @Auth final Authenticated authenticated,
            @ModelAttribute @Valid final GetImagePresignedUrlRequest request
    ) {
        final PresignedUrlDetails presignedUrl = registerPresignedUrlUseCase.invoke(new RegisterPresignedUrlCommand(
                new PresignedFileData(request.fileName())
        ));
        return ResponseEntity.ok(presignedUrl);
    }

    @Operation(summary = "PDF 파일 업로드에 대한 Presigned Url 응답 Endpoint")
    @GetMapping("/presigned/pdf")
    public ResponseEntity<PresignedUrlDetails> getPdfPresignedUrl(
            @Auth final Authenticated authenticated,
            @ModelAttribute @Valid final GetPdfPresignedUrlRequest request
    ) {
        final PresignedUrlDetails presignedUrl = registerPresignedUrlUseCase.invoke(new RegisterPresignedUrlCommand(
                new PresignedFileData(request.fileName())
        ));
        return ResponseEntity.ok(presignedUrl);
    }

    @Operation(summary = "파일 업로드 Endpoint - Deprecated")
    @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseWrapper<String>> upload(
            @Auth final Authenticated authenticated,
            @RequestPart final MultipartFile file
    ) {
        final String uploadUrl = uploadFileUseCase.invoke(new UploadFileCommand(FileConverter.convertFile(file)));
        return ResponseEntity.ok(ResponseWrapper.from(uploadUrl));
    }
}
