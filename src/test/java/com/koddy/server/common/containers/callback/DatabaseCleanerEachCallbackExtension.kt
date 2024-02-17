package com.koddy.server.common.containers.callback

import com.koddy.server.common.utils.DatabaseCleaner
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.test.context.junit.jupiter.SpringExtension

class DatabaseCleanerEachCallbackExtension : AfterEachCallback {
    override fun afterEach(context: ExtensionContext) {
        SpringExtension
            .getApplicationContext(context)
            .getBean(DatabaseCleaner::class.java)
            .cleanUpDatabase()
    }
}
