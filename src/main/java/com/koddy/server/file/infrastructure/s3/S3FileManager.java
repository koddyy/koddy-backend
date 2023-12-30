package com.koddy.server.file.infrastructure.s3;

import com.koddy.server.file.application.adapter.FileManager;
import com.koddy.server.file.domain.model.FileExtension;
import com.koddy.server.file.domain.model.PresignedFileData;
import com.koddy.server.file.domain.model.PresignedUrlDetails;
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
import java.time.Duration;
import java.util.UUID;

import static com.koddy.server.file.exception.FileExceptionCode.UPLOAD_FAILURE;
import static com.koddy.server.file.infrastructure.BucketMetadata.MEMBER_PROFILE;
import static com.koddy.server.member.domain.model.Member.EMPTY;

@Slf4j
@Component
public class S3FileManager implements FileManager {
    private final S3Template s3Template;
    private final String bucket;

    public S3FileManager(
            final S3Template s3Template,
            @Value("${spring.cloud.aws.s3.bucket}") final String bucket
    ) {
        this.s3Template = s3Template;
        this.bucket = bucket;
    }

    @Override
    public PresignedUrlDetails getPresignedUrl(final PresignedFileData file) {
        final String uploadFileName = createUploadFileName(file);
        final String preSignedUrl = createPresignedUrl(createBucketKey(uploadFileName));
        return new PresignedUrlDetails(preSignedUrl, uploadFileName);
    }

    private String createUploadFileName(final PresignedFileData file) {
        final FileExtension extension = FileExtension.getExtensionViaFimeName(file.fileName());
        return UUID.randomUUID() + extension.getValue();
    }

    private String createPresignedUrl(final String bucketKey) {
        return s3Template.createSignedPutURL(bucket, bucketKey, Duration.ofMinutes(5)).toString();
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

            final String uploadFileName = UUID.randomUUID() + file.extension().getValue();
            return s3Template.upload(bucket, createBucketKey(uploadFileName), inputStream, objectMetadata)
                    .getURL()
                    .toString();
        } catch (final IOException e) {
            log.error("File Upload Failure... ", e);
            throw new FileException(UPLOAD_FAILURE);
        }
    }

    private String createBucketKey(final String uploadFileName) {
        return String.format(MEMBER_PROFILE, uploadFileName);
    }
}
