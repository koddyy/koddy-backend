package com.koddy.server.common.containers.callback

import com.koddy.server.common.utils.RedisCleaner
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.test.context.junit.jupiter.SpringExtension

class RedisCleanerEachCallbackExtension : AfterEachCallback {
    override fun afterEach(context: ExtensionContext) {
        SpringExtension.getApplicationContext(context)
            .getBean(RedisCleaner::class.java)
            .cleanUpRedis()
    }
}
