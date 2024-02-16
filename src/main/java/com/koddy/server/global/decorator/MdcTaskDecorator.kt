package com.koddy.server.global.decorator

import org.slf4j.MDC
import org.springframework.core.task.TaskDecorator

class MdcTaskDecorator : TaskDecorator {
    override fun decorate(runnable: Runnable): Runnable {
        val copyOfContextMap: Map<String, String>? = MDC.getCopyOfContextMap()
        return Runnable {
            if (copyOfContextMap.isNullOrEmpty()) {
                runnable.run()
            }
            MDC.setContextMap(copyOfContextMap)
        }
    }
}
