package com.koddy.server.file.infrastructure.s3;

import com.koddy.server.file.application.adapter.FileUploader;
import com.koddy.server.file.domain.model.FileExtension;
import com.koddy.server.file.domain.model.RawFileData;
import com.koddy.server.file.exception.FileException;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static com.koddy.server.file.exception.FileExceptionCode.UPLOAD_FAILURE;
import static com.koddy.server.file.infrastructure.BucketMetadata.MEMBER_PROFILE;

@Slf4j
@Component
public class S3FileUploader implements FileUploader {
    private static final String EMPTY = "EMPTY";

    private final S3Template s3Template;
    private final String bucket;

    public S3FileUploader(
            final S3Template s3Template,
            @Value("${spring.cloud.aws.s3.bucket}") final String bucket
    ) {
        this.s3Template = s3Template;
        this.bucket = bucket;
    }

    @Override
    public String upload(final RawFileData file) {
        if (file == null) {
            return EMPTY;
        }
        return sendFileToStorage(file);
    }

    private String sendFileToStorage(final RawFileData file) {
        try (final InputStream inputStream = file.content()) {
            final ObjectMetadata objectMetadata = ObjectMetadata.builder()
                    .contentType(file.contenType())
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();
            final String uploadFileName = createFileNameByType(file.extension());

            return s3Template.upload(bucket, uploadFileName, inputStream, objectMetadata)
                    .getURL()
                    .toString();
        } catch (final IOException e) {
            log.error("Failure File Upload... ", e);
            throw new FileException(UPLOAD_FAILURE);
        }
    }

    private String createFileNameByType(final FileExtension fileExtension) {
        final String uploadFileName = UUID.randomUUID() + fileExtension.getValue();
        return String.format(MEMBER_PROFILE, uploadFileName);
    }
}
