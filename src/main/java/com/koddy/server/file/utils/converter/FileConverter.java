package com.koddy.server.file.utils.converter;

import com.koddy.server.file.domain.model.FileExtension;
import com.koddy.server.file.domain.model.RawFileData;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class FileConverter {
    public static RawFileData convertFile(final MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            final String fileName = file.getOriginalFilename();

            return new RawFileData(
                    fileName,
                    file.getContentType(),
                    FileExtension.getExtensionViaFimeName(fileName),
                    file.getInputStream()
            );
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
