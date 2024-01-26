package com.koddy.server.member.domain.service;

import com.koddy.server.common.IntegrateTest;
import com.koddy.server.member.domain.model.AvailableLanguage;
import com.koddy.server.member.domain.model.Member;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.model.mentor.Schedule;
import com.koddy.server.member.domain.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.member.domain.model.Member.Status.ACTIVE;
import static com.koddy.server.member.domain.model.Member.Status.INACTIVE;
import static com.koddy.server.member.domain.model.Nationality.KOREA;
import static com.koddy.server.member.domain.model.Role.MENTOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        assertAll(
                () -> assertThat(getLanguage(mentor.getId())).hasSize(MENTOR_1.getLanguages().size()),
                () -> assertThat(getSchedule(mentor.getId())).hasSize(MENTOR_1.getTimelines().size()),
                () -> {
                    assertThat(getMentorByJpql(mentor.getId())).isNotNull();
                    assertThat(getMentorByNative(mentor.getId())).isNotNull();
                },
                () -> {
                    final Mentor findMentor = getMentorByJpql(mentor.getId());
                    assertAll(
                            () -> assertThat(findMentor.getUniversityProfile().getSchool()).isEqualTo(MENTOR_1.getUniversityProfile().getSchool()),
                            () -> assertThat(findMentor.getUniversityProfile().getMajor()).isEqualTo(MENTOR_1.getUniversityProfile().getMajor()),
                            () -> assertThat(findMentor.getUniversityProfile().getEnteredIn()).isEqualTo(MENTOR_1.getUniversityProfile().getEnteredIn())
                    );

                    final Member<?> findMember = getMemberByJpql(mentor.getId());
                    assertAll(
                            () -> assertThat(findMember).isNotNull(),
                            () -> assertThat(findMember.getEmail().getValue()).isEqualTo(MENTOR_1.getEmail().getValue()),
                            () -> assertThat(findMember.getName()).isEqualTo(MENTOR_1.getName()),
                            () -> assertThat(findMember.getProfileImageUrl()).isEqualTo(MENTOR_1.getProfileImageUrl()),
                            () -> assertThat(findMember.getNationality()).isEqualTo(KOREA),
                            () -> assertThat(findMember.getIntroduction()).isEqualTo(MENTOR_1.getIntroduction()),
                            () -> assertThat(findMember.profileComplete()).isTrue(),
                            () -> assertThat(findMember.getRole()).isEqualTo(MENTOR),
                            () -> assertThat(findMember.getStatus()).isEqualTo(ACTIVE)
                    );
                }
        );

        // when
        sut.execute(mentor.getId());

        // then
        assertAll(
                () -> assertThat(getLanguage(mentor.getId())).hasSize(0),
                () -> assertThat(getSchedule(mentor.getId())).hasSize(0),
                () -> {
                    assertThatThrownBy(() -> getMentorByJpql(mentor.getId())).isInstanceOf(NoResultException.class);
                    assertThat(getMentorByNative(mentor.getId())).isNotNull();
                },
                () -> {
                    final Mentor findMentor = getMentorByNative(mentor.getId());
                    assertAll(
                            () -> assertThat(findMentor.getUniversityProfile().getSchool()).isEqualTo(MENTOR_1.getUniversityProfile().getSchool()),
                            () -> assertThat(findMentor.getUniversityProfile().getMajor()).isEqualTo(MENTOR_1.getUniversityProfile().getMajor()),
                            () -> assertThat(findMentor.getUniversityProfile().getEnteredIn()).isEqualTo(MENTOR_1.getUniversityProfile().getEnteredIn())
                    );

                    final Member<?> findMember = getMemberByNative(mentor.getId());
                    assertAll(
                            () -> assertThat(findMember).isNotNull(),
                            () -> assertThat(findMember.getEmail()).isNull(),
                            () -> assertThat(findMember.getName()).isEqualTo(MENTOR_1.getName()),
                            () -> assertThat(findMember.getProfileImageUrl()).isEqualTo(MENTOR_1.getProfileImageUrl()),
                            () -> assertThat(findMember.getNationality()).isEqualTo(KOREA),
                            () -> assertThat(findMember.getIntroduction()).isEqualTo(MENTOR_1.getIntroduction()),
                            () -> assertThat(findMember.profileComplete()).isFalse(),
                            () -> assertThat(findMember.getRole()).isEqualTo(MENTOR),
                            () -> assertThat(findMember.getStatus()).isEqualTo(INACTIVE)
                    );
                }
        );
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

    private Mentor getMentorByJpql(final long id) {
        return em.createQuery(
                        """
                                SELECT m
                                FROM Mentor m
                                WHERE m.id = :id
                                """,
                        Mentor.class
                ).setParameter("id", id)
                .getSingleResult();
    }

    private Mentor getMentorByNative(final long id) {
        return (Mentor) em.createNativeQuery(
                        """
                                SELECT *
                                FROM mentor m1
                                INNER JOIN member m2 ON m1.id = m2.id
                                WHERE m1.id = :id
                                """,
                        Mentor.class
                ).setParameter("id", id)
                .getSingleResult();
    }

    private Member<?> getMemberByJpql(final long id) {
        return memberRepository.getById(id);
    }

    private Member<?> getMemberByNative(final long id) {
        return (Member<?>) em.createNativeQuery(
                        """
                                SELECT *
                                FROM member m
                                LEFT JOIN mentor tor ON m.id = tor.id
                                LEFT JOIN mentee tee ON m.id = tee.id
                                WHERE m.id = :id
                                """,
                        Member.class
                ).setParameter("id", id)
                .getSingleResult();
    }
}
