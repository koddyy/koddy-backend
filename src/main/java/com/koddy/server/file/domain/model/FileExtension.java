package com.koddy.server.file.domain.model;

import com.koddy.server.file.exception.FileException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static com.koddy.server.file.exception.FileExceptionCode.INVALID_FILE_EXTENSION;

@Getter
@RequiredArgsConstructor
public enum FileExtension {
    JPG(".jpg"),
    JPEG(".jpeg"),
    PNG(".png"),
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
