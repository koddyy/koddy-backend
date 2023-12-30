package com.koddy.server.file.domain.model;

public record PresignedUrlDetails(
        String preSignedUrl,
        String uploadFileUrl
) {
}
