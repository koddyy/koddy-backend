package com.koddy.server.file.presentation;

import com.koddy.server.file.application.adapter.FileManager;
import com.koddy.server.file.domain.model.PresignedFileData;
import com.koddy.server.file.domain.model.PresignedUrlDetails;
import com.koddy.server.file.presentation.dto.request.GetPresignedUrlRequest;
import com.koddy.server.file.utils.converter.FileConverter;
import com.koddy.server.global.dto.ResponseWrapper;
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

@Tag(name = "파일 관련 API")
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileManagementApiController {
    private final FileManager fileManager;

    @Operation(summary = "파일 업로드 Endpoint")
    @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseWrapper<String>> upload(
            @RequestPart final MultipartFile file
    ) {
        final String uploadUrl = fileManager.upload(FileConverter.convertFile(file));
        return ResponseEntity.ok(ResponseWrapper.from(uploadUrl));
    }

    @Operation(summary = "Presigned Url 응답 Endpoint")
    @GetMapping("/presigned")
    public ResponseEntity<PresignedUrlDetails> getPresignedUrl(
            @ModelAttribute @Valid final GetPresignedUrlRequest request
    ) {
        final PresignedUrlDetails presignedUrl = fileManager.createPresignedUrl(new PresignedFileData(request.fileName()));
        return ResponseEntity.ok(presignedUrl);
    }
}
