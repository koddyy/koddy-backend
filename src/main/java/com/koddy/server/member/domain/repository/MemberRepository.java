package com.koddy.server.member.domain.repository;

import com.koddy.server.global.annotation.KoddyWritableTransactional;
import com.koddy.server.member.domain.model.Email;
import com.koddy.server.member.domain.model.Member;
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
        return Member.MemberType.Value.MENTOR.equals(getType(id));
    }

    @KoddyWritableTransactional
    @Modifying
    @Query("""
            UPDATE Member m
            SET m.status = 'INACTIVE', m.email.value = null
            WHERE m.id = :id
            """)
    void deleteMember(@Param("id") final Long id);

    // Query Method
    boolean existsById(final Long id);

    boolean existsByEmail(final Email email);

    Optional<Member> findByEmailValue(final String email);
}
