package com.koddy.server.member.domain.repository;

import com.koddy.server.common.RepositoryTest;
import com.koddy.server.member.domain.model.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.member.domain.model.MemberStatus.INACTIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member -> MemberRepository 테스트")
class MemberRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberRepository sut;

    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("사용자 Type(Mentor, Mentee)를 조회한다")
    void getType() {
        // given
        final Member<?> memberA = sut.save(MENTOR_1.toDomain());
        final Member<?> memberB = sut.save(MENTEE_1.toDomain());

        // when
        final String typeA = sut.getType(memberA.getId());
        final String typeB = sut.getType(memberB.getId());

        // then
        assertAll(
                () -> assertThat(typeA).isEqualTo(Member.MemberType.Value.MENTOR),
                () -> assertThat(sut.isMentorType(memberA.getId())).isTrue(),
                () -> assertThat(typeB).isEqualTo(Member.MemberType.Value.MENTEE),
                () -> assertThat(sut.isMentorType(memberB.getId())).isFalse()
        );
    }

    @Test
    @DisplayName("이메일 기반 사용자를 조회한다")
    void findByEmailValue() {
        // given
        final Member<?> member = sut.save(MENTOR_1.toDomain());

        // when
        final Optional<Member> actual1 = sut.findByEmailValue(member.getEmail().getValue());
        final Optional<Member> actual2 = sut.findByEmailValue("diff" + member.getEmail().getValue());

        // then
        assertAll(
                () -> assertThat(actual1).isPresent(),
                () -> assertThat(actual2).isEmpty()
        );
    }

    @Test
    @DisplayName("해당 이메일을 사용하고 있는 사용자가 존재하는지 확인한다")
    void existsByEmailValue() {
        // given
        final Member<?> member = sut.save(MENTOR_1.toDomain());

        // when
        final boolean actual1 = sut.existsByEmailValue(member.getEmail().getValue());
        final boolean actual2 = sut.existsByEmailValue("diff" + member.getEmail().getValue());

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("사용자를 삭제한다 (Soft Delete)")
    void deleteMember() {
        // given
        final Member<?> member = sut.save(MENTOR_1.toDomain());

        // when
        sut.deleteMember(member.getId());

        // then
        em.flush();
        em.clear();
        assertAll(
                () -> assertThat(getMemberByJpql(member.getId())).isEmpty(),
                () -> {
                    final Member<?> findMember = getMemberByNative(member.getId());
                    assertAll(
                            () -> assertThat(findMember).isNotNull(),
                            () -> assertThat(findMember.getEmail()).isNull(),
                            () -> assertThat(findMember.getStatus()).isEqualTo(INACTIVE)
                    );
                }
        );
    }

    private Optional<Member<?>> getMemberByJpql(final long id) {
        return sut.findById(id);
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
