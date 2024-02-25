package com.koddy.server.member.domain.repository;

import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.exception.MemberException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import static com.koddy.server.member.exception.MemberExceptionCode.MENTEE_NOT_FOUND;

public interface MenteeRepository extends JpaRepository<Mentee, Long> {
    default Mentee getById(final Long id) {
        return findById(id)
                .orElseThrow(() -> new MemberException(MENTEE_NOT_FOUND));
    }

    @Query(
            value = """
                    SELECT *
                    FROM mentee m1
                    INNER JOIN member m2 ON m1.id = m2.id
                    WHERE m1.id = :id
                    """,
            nativeQuery = true
    )
    Optional<Mentee> findByIdWithNative(@Param("id") final Long id);

    default Mentee getByIdWithNative(final Long id) {
        return findByIdWithNative(id)
                .orElseThrow(() -> new MemberException(MENTEE_NOT_FOUND));
    }

    @Query("""
            SELECT m
            FROM Mentee m
            JOIN FETCH m.availableLanguages
            WHERE m.id = :id
            """)
    Optional<Mentee> findProfile(@Param("id") final Long id);

    default Mentee getProfile(final Long id) {
        return findProfile(id)
                .orElseThrow(() -> new MemberException(MENTEE_NOT_FOUND));
    }
}
