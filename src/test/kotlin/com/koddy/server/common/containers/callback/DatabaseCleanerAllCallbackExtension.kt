package com.koddy.server.common.containers.callback

import com.koddy.server.common.utils.DatabaseCleaner
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.platform.commons.support.ModifierSupport
import org.springframework.test.context.junit.jupiter.SpringExtension

class DatabaseCleanerAllCallbackExtension : AfterAllCallback {
    override fun afterAll(context: ExtensionContext) {
        if (context.testClass.isPresent) {
            val currentClass: Class<*> = context.testClass.get()
            if (isNestedClass(currentClass)) {
                return
            }
        }
        SpringExtension
            .getApplicationContext(context)
            .getBean(DatabaseCleaner::class.java)
            .cleanUpDatabase()
    }

    private fun isNestedClass(currentClass: Class<*>): Boolean = !ModifierSupport.isStatic(currentClass) && currentClass.isMemberClass
}
