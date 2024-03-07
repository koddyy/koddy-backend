package com.koddy.server.common.config

import com.koddy.server.auth.domain.model.code.AuthCodeGenerator
import com.koddy.server.file.domain.model.BucketFileNameGenerator
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
class BlackboxLogicControlConfig {
    @Bean
    @Primary
    fun authCodeGenerator(): AuthCodeGenerator = AuthCodeGenerator { AUTH_CODE }

    @Bean
    @Primary
    fun bucketFileNameGenerator(): BucketFileNameGenerator = BucketFileNameGenerator { BUCKET_UPLOAD_PREFIX + it }

    companion object {
        const val AUTH_CODE: String = "123456"
        const val BUCKET_UPLOAD_PREFIX: String = "bucket-upload-"
    }
}
