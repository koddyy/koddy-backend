package com.koddy.server.file.infrastructure.s3

import com.koddy.server.file.application.adapter.FileManager
import com.koddy.server.file.domain.model.BucketFileNameGenerator
import com.koddy.server.file.domain.model.PresignedFileData
import com.koddy.server.file.domain.model.PresignedUrlDetails
import com.koddy.server.file.domain.model.RawFileData
import com.koddy.server.file.infrastructure.BucketPath
import com.koddy.server.global.exception.GlobalException
import com.koddy.server.global.exception.GlobalExceptionCode
import com.koddy.server.global.log.logger
import io.awspring.cloud.s3.ObjectMetadata
import io.awspring.cloud.s3.S3Template
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.s3.model.ObjectCannedACL
import java.io.IOException
import java.net.URL
import java.time.Duration

@Component
class S3FileManager(
    private val s3Template: S3Template,
    private val bucketFileNameGenerator: BucketFileNameGenerator,
    @Value("\${spring.cloud.aws.s3.bucket}") private val bucket: String,
) : FileManager {
    private val log: Logger = logger()

    override fun createPresignedUrl(file: PresignedFileData): PresignedUrlDetails {
        val uploadFileName: String = bucketFileNameGenerator.get(file.fileName)
        val preSignedUrl: URL = s3Template.createSignedPutURL(
            bucket,
            BucketPath.MEMBER_PROFILE.completePath(uploadFileName),
            Duration.ofMinutes(5),
        )
        return PresignedUrlDetails(
            preSignedUrl.toString(),
            createUploadUrl(preSignedUrl, BucketPath.MEMBER_PROFILE, uploadFileName),
        )
    }

    private fun createUploadUrl(
        preSignedUrl: URL,
        path: BucketPath,
        uploadFileName: String,
    ): String = "${preSignedUrl.protocol}://${preSignedUrl.host}/${path.path}/$uploadFileName"

    override fun upload(file: RawFileData): String {
        return sendFileToStorage(file)
    }

    private fun sendFileToStorage(file: RawFileData): String {
        try {
            file.content.use {
                val objectMetadata: ObjectMetadata = ObjectMetadata.builder()
                    .contentType(file.contentType)
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build()
                val uploadFileName: String = bucketFileNameGenerator.get(file.fileName)

                return s3Template.upload(
                    bucket,
                    BucketPath.MEMBER_PROFILE.completePath(uploadFileName),
                    it,
                    objectMetadata,
                ).url.toString()
            }
        } catch (e: IOException) {
            log.error("File Upload Failure... ", e)
            throw GlobalException(GlobalExceptionCode.UNEXPECTED_SERVER_ERROR)
        }
    }
}
