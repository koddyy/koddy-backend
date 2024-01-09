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

    @Query("""
            SELECT m
            FROM Mentee m
            JOIN FETCH m.availableLanguages
            WHERE m.id = :id
            """)
    Optional<Mentee> findProfile(@Param("id") final long id);

    default Mentee getProfile(final long id) {
        return findProfile(id)
                .orElseThrow(() -> new MemberException(MENTEE_NOT_FOUND));
    }
}
