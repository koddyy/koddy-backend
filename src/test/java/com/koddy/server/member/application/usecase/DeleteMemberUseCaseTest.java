package com.koddy.server.member.application.usecase;

import com.koddy.server.common.UnitTest;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MemberRepository;
import com.koddy.server.member.domain.service.MenteeDeleter;
import com.koddy.server.member.domain.service.MentorDeleter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Member -> DeleteMemberUseCase 테스트")
class DeleteMemberUseCaseTest extends UnitTest {
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final MentorDeleter mentorDeleter = mock(MentorDeleter.class);
    private final MenteeDeleter menteeDeleter = mock(MenteeDeleter.class);
    private final DeleteMemberUseCase sut = new DeleteMemberUseCase(
            memberRepository,
            mentorDeleter,
            menteeDeleter
    );

    private final Mentor mentor = MENTOR_1.toDomain().apply(1L);
    private final Mentee mentee = MENTEE_1.toDomain().apply(2L);

    @Test
    @DisplayName("멘토를 삭제한다")
    void deleteMentor() {
        // given
        given(memberRepository.getById(mentor.getId())).willReturn(mentor);

        // when
        sut.invoke(mentor.getId());

        // then
        assertAll(
                () -> verify(memberRepository, times(1)).getById(mentor.getId()),
                () -> verify(mentorDeleter, times(1)).execute(mentor.getId()),
                () -> verify(menteeDeleter, times(0)).execute(mentor.getId())
        );
    }

    @Test
    @DisplayName("멘티를 삭제한다")
    void deleteMentee() {
        // given
        given(memberRepository.getById(mentee.getId())).willReturn(mentee);

        // when
        sut.invoke(mentee.getId());

        // then
        assertAll(
                () -> verify(memberRepository, times(1)).getById(mentee.getId()),
                () -> verify(mentorDeleter, times(0)).execute(mentee.getId()),
                () -> verify(menteeDeleter, times(1)).execute(mentee.getId())
        );
    }
}
