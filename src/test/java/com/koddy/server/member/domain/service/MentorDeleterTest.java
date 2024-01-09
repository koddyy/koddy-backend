package com.koddy.server.member.domain.service;

import com.koddy.server.auth.domain.model.Token;
import com.koddy.server.common.IntegrateTest;
import com.koddy.server.member.domain.model.AvailableLanguage;
import com.koddy.server.member.domain.model.Member;
import com.koddy.server.member.domain.model.Role;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.model.mentor.Schedule;
import com.koddy.server.member.domain.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.member.domain.model.MemberStatus.INACTIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member -> MentorDeleter 테스트")
class MentorDeleterTest extends IntegrateTest {
    @Autowired
    private MentorDeleter sut;

    @Autowired
    private MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("Mentor를 삭제한다 (Member - Soft Delete)")
    void execute() {
        // given
        final Member<?> mentor = memberRepository.save(MENTOR_1.toDomain());

        // when
        sut.execute(mentor.getId());

        // then
        assertAll(
                () -> assertThat(getToken(mentor.getId())).hasSize(0),
                () -> assertThat(getRole(mentor.getId())).hasSize(0),
                () -> assertThat(getLanguage(mentor.getId())).hasSize(0),
                () -> assertThat(getSchedule(mentor.getId())).hasSize(0),
                () -> assertThat(getMentor(mentor.getId())).hasSize(0),
                () -> assertThat(getMemberByJpql(mentor.getId())).isEmpty(),
                () -> {
                    final Member<?> findMember = getMemberByNative(mentor.getId());
                    assertAll(
                            () -> assertThat(findMember).isNotNull(),
                            () -> assertThat(findMember.getEmail()).isNull(),
                            () -> assertThat(findMember.getStatus()).isEqualTo(INACTIVE)
                    );
                }
        );
    }

    private List<Token> getToken(final long id) {
        return em.createQuery(
                        """
                                SELECT t
                                FROM Token t
                                WHERE t.memberId = :id
                                """,
                        Token.class
                ).setParameter("id", id)
                .getResultList();
    }

    private List<Role> getRole(final long id) {
        return em.createQuery(
                        """
                                SELECT r
                                FROM Role r
                                WHERE r.member.id = :id
                                """,
                        Role.class
                ).setParameter("id", id)
                .getResultList();
    }

    private List<AvailableLanguage> getLanguage(final long id) {
        return em.createQuery(
                        """
                                SELECT al
                                FROM AvailableLanguage al
                                WHERE al.member.id = :id
                                """,
                        AvailableLanguage.class
                ).setParameter("id", id)
                .getResultList();
    }

    private List<Schedule> getSchedule(final long id) {
        return em.createQuery(
                        """
                                SELECT s
                                FROM Schedule s
                                WHERE s.mentor.id = :id
                                """,
                        Schedule.class
                ).setParameter("id", id)
                .getResultList();
    }

    private List<Mentor> getMentor(final long id) {
        return em.createQuery(
                        """
                                SELECT m
                                FROM Mentor m
                                WHERE m.id = :id
                                """,
                        Mentor.class
                ).setParameter("id", id)
                .getResultList();
    }

    private Optional<Member<?>> getMemberByJpql(final long id) {
        return memberRepository.findById(id);
    }

    private Member<?> getMemberByNative(final long id) {
        return (Member<?>) em.createNativeQuery(
                        """
                                SELECT *
                                FROM member
                                LEFT JOIN mentor m on member.id = m.id
                                LEFT JOIN mentee m2 on member.id = m2.id
                                WHERE member.id = :id
                                """,
                        Member.class
                ).setParameter("id", id)
                .getSingleResult();
    }
}
