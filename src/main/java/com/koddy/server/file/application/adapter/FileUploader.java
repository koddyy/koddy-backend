package com.koddy.server.file.application.adapter;

import com.koddy.server.file.domain.model.RawFileData;

public interface FileUploader {
    String upload(final RawFileData file);
}
