package com.koddy.server.coffeechat.domain.service

import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.AUTO_CANCEL_FROM_MENTEE_FLOW
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.AUTO_CANCEL_FROM_MENTOR_FLOW
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_APPLY
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_APPLY_COFFEE_CHAT_COMPLETE
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_PENDING
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_APPROVE
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_APPROVE
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE
import com.koddy.server.global.annotation.KoddyWritableTransactional
import com.koddy.server.global.log.logger
import org.slf4j.Logger
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class UpdateCoffeeChatStatusProcessor(
    private val coffeeChatReader: CoffeeChatReader,
    private val coffeeChatWriter: CoffeeChatWriter,
) {
    private val log: Logger = logger()

    @KoddyWritableTransactional
    fun updateWaitingToAutoCancel(standard: LocalDateTime) {
        // 1. MenteeFlow 신청
        val applyCoffeeChatIds: List<Long> = coffeeChatReader.findIdsByStatusAndPassedDeadline(MENTEE_APPLY, standard)
        log.info("[updateWaitingToAutoCancel] MENTEE_APPLY Ids = {}", applyCoffeeChatIds)
        updateStatus(applyCoffeeChatIds, AUTO_CANCEL_FROM_MENTEE_FLOW)

        // 2. MentorFlow 1차 수락
        val pendingCoffeeChatIds: List<Long> = coffeeChatReader.findIdsByStatusAndPassedDeadline(MENTEE_PENDING, standard)
        log.info("[updateWaitingToAutoCancel] MENTEE_PENDING Ids = {}", pendingCoffeeChatIds)
        updateStatus(pendingCoffeeChatIds, AUTO_CANCEL_FROM_MENTOR_FLOW)
    }

    @KoddyWritableTransactional
    fun updateScheduledToComplete(standard: LocalDateTime) {
        // 1. MenteeFlow 수락
        val approveCoffeeChatIds: List<Long> = coffeeChatReader.findIdsByStatusAndPassedDeadline(MENTOR_APPROVE, standard)
        log.info("[updateScheduledToComplete] MENTOR_APPROVE Ids = {}", approveCoffeeChatIds)
        updateStatus(approveCoffeeChatIds, MENTEE_APPLY_COFFEE_CHAT_COMPLETE)

        // 2. MentorFlow 수락
        val finallyApproveCoffeeChatIds: List<Long> = coffeeChatReader.findIdsByStatusAndPassedDeadline(MENTOR_FINALLY_APPROVE, standard)
        log.info("[updateScheduledToComplete] MENTOR_FINALLY_APPROVE Ids = {}", finallyApproveCoffeeChatIds)
        updateStatus(finallyApproveCoffeeChatIds, MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE)
    }

    private fun updateStatus(
        coffeeChatIds: List<Long>,
        updateStatus: CoffeeChatStatus,
    ) {
        if (coffeeChatIds.isNotEmpty()) {
            coffeeChatWriter.updateStatusInBatch(coffeeChatIds, updateStatus)
        }
    }
}
