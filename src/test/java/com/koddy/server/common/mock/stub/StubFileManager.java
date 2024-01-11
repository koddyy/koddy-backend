package com.koddy.server.common.mock.stub;


import com.koddy.server.file.application.adapter.FileManager;
import com.koddy.server.file.domain.model.PresignedFileData;
import com.koddy.server.file.domain.model.PresignedUrlDetails;
import com.koddy.server.file.domain.model.RawFileData;

public class StubFileManager implements FileManager {
    @Override
    public PresignedUrlDetails createPresignedUrl(final PresignedFileData file) {
        return new PresignedUrlDetails(
                "https://pre-signed-url/" + file.fileName(),
                "https://pre-signed-url-upload/" + file.fileName()
        );
    }

    @Override
    public String upload(final RawFileData file) {
        return "https://upload-url/" + file.fileName();
    }
}
