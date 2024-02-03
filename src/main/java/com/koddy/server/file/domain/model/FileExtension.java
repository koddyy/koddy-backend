package com.koddy.server.file.domain.model;

import com.koddy.server.file.exception.FileException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.stream.Stream;

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

    public static FileExtension from(final String fileName) {
        return Arrays.stream(values())
                .filter(it -> it.value.equals(extractFileExtension(fileName)))
                .findFirst()
                .orElseThrow(() -> new FileException(INVALID_FILE_EXTENSION));
    }

    public static boolean isImage(final String fileName) {
        return Stream.of(JPG, JPEG, PNG)
                .anyMatch(it -> it.value.equals(extractFileExtension(fileName)));
    }

    public static boolean isPdf(final String fileName) {
        return PDF.value.equals(extractFileExtension(fileName));
    }

    private static String extractFileExtension(final String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
