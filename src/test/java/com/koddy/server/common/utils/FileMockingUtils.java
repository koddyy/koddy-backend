package com.koddy.server.common.utils;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;

public class FileMockingUtils {
    private static final String FILE_PATH = "src/test/resources/files/";
    private static final String SINGLE_FILE_META_NAME = "file";

    public static MultipartFile createFile(final String fileName, final String contentType) {
        try (final FileInputStream stream = new FileInputStream(FILE_PATH + fileName)) {
            return new MockMultipartFile(SINGLE_FILE_META_NAME, fileName, contentType, stream);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
