package com.koddy.server.member.domain.repository;

import com.koddy.server.global.annotation.KoddyWritableTransactional;
import com.koddy.server.member.domain.model.AvailableLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AvailableLanguageRepository extends JpaRepository<AvailableLanguage, Long> {
    @KoddyWritableTransactional
    @Modifying
    @Query("DELETE FROM AvailableLanguage al WHERE al.member.id = :id")
    void deleteMemberLanguage(@Param("id") final long id);
}
