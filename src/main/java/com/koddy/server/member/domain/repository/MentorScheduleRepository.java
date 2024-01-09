package com.koddy.server.member.domain.repository;

import com.koddy.server.global.annotation.KoddyWritableTransactional;
import com.koddy.server.member.domain.model.mentor.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MentorScheduleRepository extends JpaRepository<Schedule, Long> {
    @KoddyWritableTransactional
    @Modifying
    @Query("DELETE FROM Schedule s WHERE s.mentor.id = :id")
    void deleteMentorSchedule(@Param("id") final long id);
}
