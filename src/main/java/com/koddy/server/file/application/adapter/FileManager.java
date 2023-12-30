package com.koddy.server.file.application.adapter;

import com.koddy.server.file.domain.model.PresignedFileData;
import com.koddy.server.file.domain.model.PresignedUrlDetails;
import com.koddy.server.file.domain.model.RawFileData;

public interface FileManager {
    PresignedUrlDetails createPresignedUrl(final PresignedFileData file);

    String upload(final RawFileData file);
}
