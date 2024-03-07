package com.koddy.server.member.domain.repository

import com.koddy.server.global.annotation.KoddyWritableTransactional
import com.koddy.server.member.domain.model.mentor.Schedule
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface MentorScheduleRepository : JpaRepository<Schedule, Long> {
    @KoddyWritableTransactional
    @Modifying
    @Query("DELETE FROM Schedule s WHERE s.mentor.id = :id")
    fun deleteMentorSchedule(@Param("id") id: Long)
}
