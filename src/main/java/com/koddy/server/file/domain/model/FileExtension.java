package com.koddy.server.file.domain.model;

import com.koddy.server.file.exception.FileException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static com.koddy.server.file.exception.FileExceptionCode.INVALID_FILE_EXTENSION;

@Getter
@RequiredArgsConstructor
public enum FileExtension {
    // 프로필 사진
    JPG(".jpg"),
    JPEG(".jpeg"),
    PNG(".png"),

    // 멘토 학교 증명자료
    PDF(".pdf"),
    ;

    private final String value;

    public static FileExtension getExtensionViaFimeName(final String fileName) {
        return Arrays.stream(values())
                .filter(extension -> extension.value.equals(extractFileExtension(fileName)))
                .findFirst()
                .orElseThrow(() -> new FileException(INVALID_FILE_EXTENSION));
    }

    private static String extractFileExtension(final String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
