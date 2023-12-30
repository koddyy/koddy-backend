package com.koddy.server.file.infrastructure.s3;

import com.koddy.server.file.application.adapter.FileManager;
import com.koddy.server.file.domain.model.FileExtension;
import com.koddy.server.file.domain.model.PresignedFileData;
import com.koddy.server.file.domain.model.PresignedUrlDetails;
import com.koddy.server.file.domain.model.RawFileData;
import com.koddy.server.file.exception.FileException;
import com.koddy.server.file.infrastructure.BucketPath;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;

import static com.koddy.server.file.exception.FileExceptionCode.UPLOAD_FAILURE;
import static com.koddy.server.file.infrastructure.BucketPath.MEMBER_PROFILE;
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
    public PresignedUrlDetails createPresignedUrl(final PresignedFileData file) {
        final String uploadFileName = createUploadFileName(file);

        final URL preSignedUrl = s3Template.createSignedPutURL(
                bucket,
                MEMBER_PROFILE.completePath(uploadFileName),
                Duration.ofMinutes(5)
        );
        return new PresignedUrlDetails(
                preSignedUrl.toString(),
                createUploadUrlPrefix(preSignedUrl, MEMBER_PROFILE, uploadFileName)
        );
    }

    private String createUploadFileName(final PresignedFileData file) {
        final FileExtension extension = FileExtension.getExtensionViaFimeName(file.fileName());
        return UUID.randomUUID() + extension.getValue();
    }

    private String createUploadUrlPrefix(final URL preSignedUrl, final BucketPath path, final String uploadFileName) {
        return preSignedUrl.getProtocol()
                + "://" + preSignedUrl.getHost()
                + "/" + path.getPath()
                + "/" + uploadFileName;
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
                    .contentType(file.contentType())
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();

            final String uploadFileName = UUID.randomUUID() + file.extension().getValue();
            return s3Template.upload(
                    bucket,
                    MEMBER_PROFILE.completePath(uploadFileName),
                    inputStream,
                    objectMetadata
            ).getURL().toString();
        } catch (final IOException e) {
            log.error("File Upload Failure... ", e);
            throw new FileException(UPLOAD_FAILURE);
        }
    }
}
