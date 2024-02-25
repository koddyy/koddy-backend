package com.koddy.server.member.domain.repository;

import com.koddy.server.member.domain.model.AvailableLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AvailableLanguageRepository extends JpaRepository<AvailableLanguage, Long> {
    @Query(
            value = """
                    SELECT *
                    FROM member_language
                    WHERE member_id = :memberId
                    """,
            nativeQuery = true
    )
    List<AvailableLanguage> findByMemberIdWithNative(@Param("memberId") final long memberId);
}
