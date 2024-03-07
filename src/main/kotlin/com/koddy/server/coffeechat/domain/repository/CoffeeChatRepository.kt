package com.koddy.server.coffeechat.domain.repository

import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus
import com.koddy.server.global.annotation.KoddyWritableTransactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface CoffeeChatRepository : JpaRepository<CoffeeChat, Long> {
    // @Query
    @Query(
        """
        SELECT c.id
        FROM CoffeeChat c
        WHERE c.status = :status
            AND c.reservation.start IS NOT NULL
            AND c.reservation.start <= :deadline
        """,
    )
    fun findIdsByStatusAndPassedDeadline(
        @Param("status") status: CoffeeChatStatus,
        @Param("deadline") deadline: LocalDateTime,
    ): List<Long>

    @KoddyWritableTransactional
    @Modifying(
        flushAutomatically = true,
        clearAutomatically = true,
    )
    @Query(
        """
        UPDATE CoffeeChat c
        SET c.status = :status
        WHERE c.id IN :ids
        """,
    )
    fun updateStatusInBatch(
        @Param("ids") ids: List<Long>,
        @Param("status") status: CoffeeChatStatus,
    )

    // Query Method
    fun findByIdAndMentorId(
        id: Long,
        mentorId: Long,
    ): CoffeeChat?

    fun findByIdAndMenteeId(
        id: Long,
        menteeId: Long,
    ): CoffeeChat?

    fun countByMentorIdAndStatusIn(
        mentorId: Long,
        status: List<CoffeeChatStatus>,
    ): Long

    fun countByMenteeIdAndStatusIn(
        menteeId: Long,
        status: List<CoffeeChatStatus>,
    ): Long
}
