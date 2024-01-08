package com.koddy.server.member.domain.repository;

import com.koddy.server.member.domain.model.mentor.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentorScheduleRepository extends JpaRepository<Schedule, Long> {
}
