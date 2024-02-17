package com.koddy.server.common.containers

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.utility.DockerImageName
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.BucketCannedACL
import software.amazon.awssdk.services.s3.model.CreateBucketRequest

@TestConfiguration
class LocalStackTestContainersConfig {
    @Bean(initMethod = "start", destroyMethod = "stop")
    fun localStackContainer(): LocalStackContainer {
        val container: LocalStackContainer =
            LocalStackContainer(LOCALSTACK_IMAGE)
                .apply {
                    withServices(LocalStackContainer.Service.S3)

                    System.setProperty("spring.cloud.aws.region.static", region)
                    System.setProperty("spring.cloud.aws.credentials.access-key", accessKey)
                    System.setProperty("spring.cloud.aws.credentials.secret-key", secretKey)
                    System.setProperty("spring.cloud.aws.s3.bucket", BUCKET_NAME)
                }
        return container
    }

    @Bean
    fun s3Client(container: LocalStackContainer): S3Client {
        val s3Client = S3Client
            .builder()
            .endpointOverride(container.endpoint)
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        container.accessKey,
                        container.secretKey
                    )
                )
            )
            .region(Region.of(container.region))
            .build().apply {
                createBucket(
                    CreateBucketRequest.builder()
                        .acl(BucketCannedACL.PUBLIC_READ)
                        .bucket(BUCKET_NAME)
                        .build()
                )
            }
        return s3Client
    }

    companion object {
        private val LOCALSTACK_IMAGE: DockerImageName = DockerImageName.parse("localstack/localstack")
        private const val BUCKET_NAME: String = "koddy-upload"
    }
}
