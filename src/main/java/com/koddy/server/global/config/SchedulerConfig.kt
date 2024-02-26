package com.koddy.server.global.config

import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.config.ScheduledTaskRegistrar

@Configuration
@EnableScheduling
class SchedulerConfig : SchedulingConfigurer {
    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        val threadPool = ThreadPoolTaskScheduler().apply {
            poolSize = 10
            setThreadNamePrefix("Scheduler Thread-")
            initialize()
        }
        taskRegistrar.setTaskScheduler(threadPool)
    }
}
