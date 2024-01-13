package com.koddy.server.member.application.usecase;

import com.koddy.server.common.UnitTest;
import com.koddy.server.common.fixture.TimelineFixture;
import com.koddy.server.member.application.usecase.command.CompleteMenteeProfileCommand;
import com.koddy.server.member.application.usecase.command.CompleteMentorProfileCommand;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MenteeRepository;
import com.koddy.server.member.domain.repository.MentorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.member.domain.model.ProfileComplete.NO;
import static com.koddy.server.member.domain.model.ProfileComplete.YES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Member -> CompleteProfileUseCase 테스트")
class CompleteProfileUseCaseTest extends UnitTest {
    private final MentorRepository mentorRepository = mock(MentorRepository.class);
    private final MenteeRepository menteeRepository = mock(MenteeRepository.class);
    private final CompleteProfileUseCase sut = new CompleteProfileUseCase(mentorRepository, menteeRepository);

    @Test
    @DisplayName("멘토 프로필을 완성한다")
    void completeMentor() {
        // given
        final Mentor mentor = new Mentor(
                MENTOR_1.getEmail(),
                MENTOR_1.getName(),
                MENTOR_1.getProfileImageUrl(),
                MENTOR_1.getLanguages(),
                MENTOR_1.getUniversityProfile()
        ).apply(1L);
        final CompleteMentorProfileCommand command = new CompleteMentorProfileCommand(
                mentor.getId(),
                MENTOR_1.getIntroduction(),
                TimelineFixture.월_수_금()
        );
        given(mentorRepository.getById(command.mentorId())).willReturn(mentor);

        assertAll(
                () -> assertThat(mentor.getIntroduction()).isNull(),
                () -> assertThat(mentor.getSchedules()).hasSize(0),
                () -> assertThat(mentor.isProfileComplete()).isFalse(),
                () -> assertThat(mentor.getProfileComplete()).isEqualTo(NO)
        );

        // when
        sut.completeMentor(command);

        // then
        assertAll(
                () -> verify(mentorRepository, times(1)).getById(command.mentorId()),
                () -> verify(menteeRepository, times(0)).getById(anyLong()),
                () -> assertThat(mentor.getIntroduction()).isNotNull(),
                () -> assertThat(mentor.getSchedules()).hasSize(TimelineFixture.월_수_금().size()),
                () -> assertThat(mentor.isProfileComplete()).isTrue(),
                () -> assertThat(mentor.getProfileComplete()).isEqualTo(YES)
        );
    }

    @Test
    @DisplayName("멘티 프로필을 완성한다")
    void completeMentee() {
        // given
        final Mentee mentee = new Mentee(
                MENTEE_1.getEmail(),
                MENTEE_1.getName(),
                MENTEE_1.getProfileImageUrl(),
                MENTEE_1.getNationality(),
                MENTEE_1.getLanguages(),
                MENTEE_1.getInterest()
        ).apply(1L);
        final CompleteMenteeProfileCommand command = new CompleteMenteeProfileCommand(mentee.getId(), MENTEE_1.getIntroduction());
        given(menteeRepository.getById(command.menteeId())).willReturn(mentee);

        assertAll(
                () -> assertThat(mentee.getIntroduction()).isNull(),
                () -> assertThat(mentee.isProfileComplete()).isFalse(),
                () -> assertThat(mentee.getProfileComplete()).isEqualTo(NO)
        );

        // when
        sut.completeMentee(command);

        // then
        assertAll(
                () -> verify(mentorRepository, times(0)).getById(anyLong()),
                () -> verify(menteeRepository, times(1)).getById(command.menteeId()),
                () -> assertThat(mentee.getIntroduction()).isNotNull(),
                () -> assertThat(mentee.isProfileComplete()).isTrue(),
                () -> assertThat(mentee.getProfileComplete()).isEqualTo(YES)
        );
    }
}
