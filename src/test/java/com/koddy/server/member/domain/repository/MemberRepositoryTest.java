package com.koddy.server.member.domain.repository;

import com.koddy.server.common.RepositoryTest;
import com.koddy.server.member.domain.model.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member -> MemberRepository 테스트")
class MemberRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberRepository sut;

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
}
