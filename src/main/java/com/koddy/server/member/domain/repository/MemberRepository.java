package com.koddy.server.member.domain.repository;

import com.koddy.server.global.annotation.KoddyWritableTransactional;
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
    default Member getById(final long id) {
        return findById(id)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
    }

    // @Query
    @Query("""
            SELECT m
            FROM Member m
            JOIN FETCH m.roles
            WHERE m.id = :id
            """)
    Optional<Member> findByIdWithRoles(@Param("id") final long id);

    default Member getByIdWithRoles(final long id) {
        return findByIdWithRoles(id)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
    }

    @Query(
            value = """
                    SELECT type
                    FROM member
                    WHERE id = :id
                    """,
            nativeQuery = true
    )
    String getType(@Param("id") long id);

    default boolean isMentorType(final long id) {
        return Member.MemberType.Value.MENTOR.equals(getType(id));
    }

    @KoddyWritableTransactional
    @Modifying
    @Query("""
            UPDATE Member m
            SET m.status = 'INACTIVE', m.email = null
            WHERE m.id = :id
            """)
    void deleteMember(@Param("id") final long id);

    // Query Method
    boolean existsById(final long id);

    boolean existsByEmailValue(final String value);

    Optional<Member> findByEmailValue(final String email);

    default Member getByEmail(final String email) {
        return findByEmailValue(email)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
    }
}
