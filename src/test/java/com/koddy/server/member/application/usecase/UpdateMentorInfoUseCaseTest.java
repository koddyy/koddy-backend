package com.koddy.server.member.application.usecase;

import com.koddy.server.common.UseCaseTest;
import com.koddy.server.common.fixture.ScheduleFixture;
import com.koddy.server.member.application.usecase.command.UpdateMentorBasicInfoCommand;
import com.koddy.server.member.application.usecase.command.UpdateMentorScheduleCommand;
import com.koddy.server.member.domain.model.mentor.ChatTime;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MentorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_2;
import static com.koddy.server.member.domain.model.Nationality.KOREA;
import static com.koddy.server.member.domain.model.RoleType.MENTOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Member -> UpdateMentorInfoUseCase 테스트")
class UpdateMentorInfoUseCaseTest extends UseCaseTest {
    private final MentorRepository mentorRepository = mock(MentorRepository.class);
    private final UpdateMentorInfoUseCase sut = new UpdateMentorInfoUseCase(mentorRepository);

    @Test
    @DisplayName("Mentor 기본 정보를 수정한다")
    void updateBasicInfo() {
        // given
        final Mentor mentor = MENTOR_1.toDomain().apply(1L);
        final UpdateMentorBasicInfoCommand command = new UpdateMentorBasicInfoCommand(
                mentor.getId(),
                MENTOR_2.getName(),
                MENTOR_2.getProfileImageUrl(),
                MENTOR_2.getIntroduction(),
                MENTOR_2.getLanguages(),
                MENTOR_2.getUniversityProfile().getSchool(),
                MENTOR_2.getUniversityProfile().getMajor(),
                MENTOR_2.getUniversityProfile().getEnteredIn()
        );
        given(mentorRepository.getById(command.mentorId())).willReturn(mentor);

        // when
        sut.updateBasicInfo(command);

        // then
        assertAll(
                () -> verify(mentorRepository, times(1)).getById(command.mentorId()),
                () -> assertThat(mentor.getName()).isEqualTo(command.name()),
                () -> assertThat(mentor.getNationality()).isEqualTo(KOREA),
                () -> assertThat(mentor.getProfileImageUrl()).isEqualTo(command.profileImageUrl()),
                () -> assertThat(mentor.getIntroduction()).isEqualTo(command.introduction()),
                () -> assertThat(mentor.getLanguages()).containsExactlyInAnyOrderElementsOf(command.languages()),
                () -> assertThat(mentor.getRoleTypes()).containsExactlyInAnyOrder(MENTOR),
                () -> assertThat(mentor.getUniversityProfile().getSchool()).isEqualTo(command.school()),
                () -> assertThat(mentor.getUniversityProfile().getMajor()).isEqualTo(command.major()),
                () -> assertThat(mentor.getUniversityProfile().getEnteredIn()).isEqualTo(command.enteredIn())
        );
    }

    @Test
    @DisplayName("Mentor 스케줄을 수정한다")
    void updateSchedule() {
        // given
        final Mentor mentor = MENTOR_1.toDomain().apply(1L);
        final UpdateMentorScheduleCommand command = new UpdateMentorScheduleCommand(
                mentor.getId(),
                ScheduleFixture.allDays()
        );
        given(mentorRepository.getById(command.mentorId())).willReturn(mentor);

        // when
        sut.updateSchedule(command);

        // then
        assertAll(
                () -> verify(mentorRepository, times(1)).getById(command.mentorId()),
                () -> assertThat(mentor.getChatTimes())
                        .map(ChatTime::getSchedule)
                        .containsExactlyInAnyOrderElementsOf(command.schedules())
        );
    }
}
