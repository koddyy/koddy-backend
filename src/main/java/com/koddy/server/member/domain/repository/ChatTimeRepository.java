package com.koddy.server.member.domain.repository;

import com.koddy.server.member.domain.model.mentor.ChatTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatTimeRepository extends JpaRepository<ChatTime, Long> {
}
