package com.koddy.server.file.domain.model;

import java.io.InputStream;

public record RawFileData(
        String fileName,
        String contentType,
        FileExtension extension,
        InputStream content
) {
}
