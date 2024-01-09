package com.koddy.server.auth.domain.repository;

import com.koddy.server.auth.domain.model.Token;
import com.koddy.server.global.annotation.KoddyWritableTransactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    // @Query
    @KoddyWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            UPDATE Token t
            SET t.refreshToken = :refreshToken
            WHERE t.memberId = :memberId
            """)
    void updateRefreshToken(@Param("memberId") final long memberId, @Param("refreshToken") final String refreshToken);

    @KoddyWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Token t WHERE t.memberId = :memberId")
    void deleteRefreshToken(@Param("memberId") final long memberId);

    // Query Method
    Optional<Token> findByMemberId(final long memberId);

    boolean existsByMemberIdAndRefreshToken(final long memberId, final String refreshToken);
}
