package com.koddy.server.member.application.usecase;

import com.koddy.server.common.UseCaseTest;
import com.koddy.server.member.domain.model.Member;
import com.koddy.server.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Member -> DuplicateCheckUseCase 테스트")
class DuplicateCheckUseCaseTest extends UseCaseTest {
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final DuplicateCheckUseCase sut = new DuplicateCheckUseCase(memberRepository);

    @Test
    @DisplayName("사용 가능한 이메일인지 확인한다 (중복 X)")
    void isEmailUsable() {
        // given
        final Member<?> memberA = MENTOR_1.toDomain().apply(1L);
        final Member<?> memberB = MENTOR_2.toDomain().apply(1L);

        given(memberRepository.existsByEmailValue(memberA.getEmail().getValue())).willReturn(true);
        given(memberRepository.existsByEmailValue(memberB.getEmail().getValue())).willReturn(false);

        // when
        final boolean result1 = sut.isEmailUsable(memberA.getEmail().getValue());
        final boolean result2 = sut.isEmailUsable(memberB.getEmail().getValue());

        // then
        Assertions.assertAll(
                () -> verify(memberRepository, times(1)).existsByEmailValue(memberA.getEmail().getValue()),
                () -> verify(memberRepository, times(1)).existsByEmailValue(memberB.getEmail().getValue()),
                () -> assertThat(result1).isFalse(),
                () -> assertThat(result2).isTrue()
        );
    }
}
