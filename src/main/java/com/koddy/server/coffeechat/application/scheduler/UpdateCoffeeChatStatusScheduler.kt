package com.koddy.server.coffeechat.application.scheduler

import com.koddy.server.coffeechat.domain.service.UpdateCoffeeChatStatusProcessor
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class UpdateCoffeeChatStatusScheduler(
    private val updateCoffeeChatStatusProcessor: UpdateCoffeeChatStatusProcessor,
) {
    @Scheduled(cron = "0 0/10 * * * *", zone = "Asia/Seoul")
    fun updateCoffeeChatStatus() {
        val now: LocalDateTime = LocalDateTime.now()
        updateCoffeeChatStatusProcessor.updateWaitingToAutoCancel(now)
        updateCoffeeChatStatusProcessor.updateScheduledToComplete(now)
    }
}
