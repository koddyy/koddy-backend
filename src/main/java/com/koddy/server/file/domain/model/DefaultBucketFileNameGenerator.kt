package com.koddy.server.file.domain.model

import org.springframework.stereotype.Component
import java.util.UUID

@Component
class DefaultBucketFileNameGenerator : BucketFileNameGenerator {
    override fun get(fileName: String): String = UUID.randomUUID().toString() + FileExtension.from(fileName).value
}
