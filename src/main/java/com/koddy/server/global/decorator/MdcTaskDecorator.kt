package com.koddy.server.global.decorator

import org.slf4j.MDC
import org.springframework.core.task.TaskDecorator

class MdcTaskDecorator : TaskDecorator {
    override fun decorate(runnable: Runnable): Runnable {
        val copyOfContextMap = MDC.getCopyOfContextMap()

        return Runnable {
            MDC.setContextMap(copyOfContextMap)
            runnable.run()
        }
    }
}
