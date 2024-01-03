package com.koddy.server.member.domain.repository;

import com.koddy.server.member.domain.model.AvailableLanguage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvailableLanguageRepository extends JpaRepository<AvailableLanguage, Long> {
}
