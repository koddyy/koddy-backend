package com.koddy.server.common.containers.callback

import com.koddy.server.common.utils.RedisCleaner
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.platform.commons.support.ModifierSupport
import org.springframework.test.context.junit.jupiter.SpringExtension

class RedisCleanerAllCallbackExtension : AfterAllCallback {
    override fun afterAll(context: ExtensionContext) {
        if (context.testClass.isPresent) {
            val currentClass: Class<*> = context.testClass.get()
            if (isNestedClass(currentClass)) {
                return
            }
        }
        SpringExtension.getApplicationContext(context)
            .getBean(RedisCleaner::class.java)
            .cleanUpRedis()
    }

    private fun isNestedClass(currentClass: Class<*>): Boolean {
        return !ModifierSupport.isStatic(currentClass) && currentClass.isMemberClass
    }
}
