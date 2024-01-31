package com.koddy.server.member.domain.repository;

import com.koddy.server.global.annotation.KoddyWritableTransactional;
import com.koddy.server.member.domain.model.Email;
import com.koddy.server.member.domain.model.Member;
import com.koddy.server.member.domain.model.Role;
import com.koddy.server.member.exception.MemberException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import static com.koddy.server.member.exception.MemberExceptionCode.MEMBER_NOT_FOUND;

@SuppressWarnings("rawtypes")
public interface MemberRepository extends JpaRepository<Member<?>, Long> {
    default Member getById(final Long id) {
        return findById(id)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
    }

    // @Query
    @Query(
            value = """
                    SELECT type
                    FROM member
                    WHERE id = :id
                    """,
            nativeQuery = true
    )
    String getType(@Param("id") Long id);

    default boolean isMentor(final Long id) {
        return Role.Value.MENTOR.equals(getType(id));
    }

    @KoddyWritableTransactional
    @Modifying
    @Query(
            value = """
                    UPDATE member
                    SET status = 'INACTIVE',
                        social_id = null,
                        email = null,
                        profile_complete = false
                    WHERE id = :id
                    """,
            nativeQuery = true
    )
    void deleteMember(@Param("id") final Long id);

    // Query Method
    boolean existsById(final Long id);

    boolean existsByPlatformSocialId(final String socialId);

    Optional<Member> findByPlatformSocialId(final String socialId);

    Optional<Member> findByPlatformEmail(final Email email);
}
