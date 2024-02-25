package com.koddy.server.member.domain.service;

import com.koddy.server.common.IntegrateTest;
import com.koddy.server.member.domain.model.AvailableLanguage;
import com.koddy.server.member.domain.model.Member;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.member.domain.model.Member.Status.ACTIVE;
import static com.koddy.server.member.domain.model.Member.Status.INACTIVE;
import static com.koddy.server.member.domain.model.Role.MENTEE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member -> MenteeDeleter 테스트 [IntegrateTest]")
class MenteeDeleterTest extends IntegrateTest {
    @Autowired
    private MenteeDeleter sut;

    @Autowired
    private MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("Mentee를 삭제한다 (Member - Soft Delete)")
    void execute() {
        // given
        final Member<?> mentee = memberRepository.save(MENTEE_1.toDomain());
        assertAll(
                () -> assertThat(getLanguage(mentee.getId())).hasSize(MENTEE_1.getLanguages().size()),
                () -> {
                    assertThat(getMenteeByJpql(mentee.getId())).isNotNull();
                    assertThat(getMenteeByNative(mentee.getId())).isNotNull();
                },
                () -> {
                    final Mentee findMentee = getMenteeByJpql(mentee.getId());
                    assertAll(
                            () -> assertThat(findMentee.getInterest().getSchool()).isEqualTo(MENTEE_1.getInterest().getSchool()),
                            () -> assertThat(findMentee.getInterest().getMajor()).isEqualTo(MENTEE_1.getInterest().getMajor())
                    );

                    final Member<?> findMember = getMemberByJpql(mentee.getId());
                    assertAll(
                            () -> assertThat(findMember).isNotNull(),
                            () -> assertThat(findMember.getPlatform().getProvider()).isEqualTo(MENTEE_1.getPlatform().getProvider()),
                            () -> assertThat(findMember.getPlatform().getSocialId()).isEqualTo(MENTEE_1.getPlatform().getSocialId()),
                            () -> assertThat(findMember.getPlatform().getEmail().getValue()).isEqualTo(MENTEE_1.getPlatform().getEmail().getValue()),
                            () -> assertThat(findMember.getName()).isEqualTo(MENTEE_1.getName()),
                            () -> assertThat(findMember.getProfileImageUrl()).isEqualTo(MENTEE_1.getProfileImageUrl()),
                            () -> assertThat(findMember.getNationality()).isEqualTo(MENTEE_1.getNationality()),
                            () -> assertThat(findMember.getIntroduction()).isEqualTo(MENTEE_1.getIntroduction()),
                            () -> assertThat(findMember.isProfileComplete()).isTrue(),
                            () -> assertThat(findMember.getRole()).isEqualTo(MENTEE),
                            () -> assertThat(findMember.getStatus()).isEqualTo(ACTIVE)
                    );
                }
        );

        // when
        sut.execute(mentee.getId());

        // then
        assertAll(
                () -> assertThat(getLanguage(mentee.getId())).hasSize(MENTEE_1.getLanguages().size()),
                () -> {
                    assertThatThrownBy(() -> getMenteeByJpql(mentee.getId())).isInstanceOf(NoResultException.class);
                    assertThat(getMenteeByNative(mentee.getId())).isNotNull();
                },
                () -> {
                    final Mentee findMentee = getMenteeByNative(mentee.getId());
                    assertAll(
                            () -> assertThat(findMentee.getInterest().getSchool()).isEqualTo(MENTEE_1.getInterest().getSchool()),
                            () -> assertThat(findMentee.getInterest().getMajor()).isEqualTo(MENTEE_1.getInterest().getMajor())
                    );

                    final Member<?> findMember = getMemberByNative(mentee.getId());
                    assertAll(
                            () -> assertThat(findMember).isNotNull(),
                            () -> assertThat(findMember.getPlatform().getProvider()).isEqualTo(MENTEE_1.getPlatform().getProvider()),
                            () -> assertThat(findMember.getPlatform().getSocialId()).isNull(),
                            () -> assertThat(findMember.getPlatform().getEmail()).isNull(),
                            () -> assertThat(findMember.getName()).isEqualTo(MENTEE_1.getName()),
                            () -> assertThat(findMember.getProfileImageUrl()).isEqualTo(MENTEE_1.getProfileImageUrl()),
                            () -> assertThat(findMember.getNationality()).isEqualTo(MENTEE_1.getNationality()),
                            () -> assertThat(findMember.getIntroduction()).isEqualTo(MENTEE_1.getIntroduction()),
                            () -> assertThat(findMember.isProfileComplete()).isFalse(),
                            () -> assertThat(findMember.getRole()).isEqualTo(MENTEE),
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

    private Mentee getMenteeByJpql(final long id) {
        return em.createQuery(
                        """
                                SELECT m
                                FROM Mentee m
                                WHERE m.id = :id
                                """,
                        Mentee.class
                ).setParameter("id", id)
                .getSingleResult();
    }

    private Mentee getMenteeByNative(final long id) {
        return (Mentee) em.createNativeQuery(
                        """
                                SELECT *
                                FROM mentee m1
                                INNER JOIN member m2 ON m1.id = m2.id
                                WHERE m1.id = :id
                                """,
                        Mentee.class
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
