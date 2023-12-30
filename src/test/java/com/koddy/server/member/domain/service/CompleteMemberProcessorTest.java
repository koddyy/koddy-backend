package com.koddy.server.member.domain.service;

import com.koddy.server.common.ServiceTest;
import com.koddy.server.file.utils.converter.FileConverter;
import com.koddy.server.member.application.usecase.command.CompleteMenteeCommand;
import com.koddy.server.member.application.usecase.command.CompleteMentorCommand;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MenteeRepository;
import com.koddy.server.member.domain.repository.MentorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.utils.FileMockingUtils.createFile;
import static com.koddy.server.member.domain.model.Nationality.ANONYMOUS;
import static com.koddy.server.member.domain.model.RoleType.MENTEE;
import static com.koddy.server.member.domain.model.RoleType.MENTOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@DisplayName("Member -> CompleteMemberProcessor 테스트")
class CompleteMemberProcessorTest extends ServiceTest {
    private final MentorRepository mentorRepository = mock(MentorRepository.class);
    private final MenteeRepository menteeRepository = mock(MenteeRepository.class);
    private final CompleteMemberProcessor sut = new CompleteMemberProcessor(mentorRepository, menteeRepository);

    private static final String EMPTY = "EMPTY";

    @Test
    @DisplayName("Mentor 부가 정보를 기입한다")
    void completeMentor() {
        // given
        final Mentor mentor = new Mentor(MENTOR_1.getEmail(), MENTOR_1.getPassword()).apply(1L);
        final CompleteMentorCommand command = new CompleteMentorCommand(
                mentor.getId(),
                MENTOR_1.getName(),
                MENTOR_1.getNationality(),
                FileConverter.convertFile(createFile("cat.png", "image/png")),
                MENTOR_1.getLanguages(),
                MENTOR_1.getUniversityProfile(),
                MENTOR_1.getMeetingUrl(),
                MENTOR_1.getIntroduction(),
                MENTOR_1.getSchedules()
        );
        given(mentorRepository.getById(command.mentorId())).willReturn(mentor);

        assertAll(
                () -> assertThat(mentor.getEmail().getValue()).isEqualTo(MENTOR_1.getEmail().getValue()),
                () -> assertThat(mentor.getPassword()).isNotNull(),
                () -> assertThat(mentor.getName()).isEqualTo(EMPTY),
                () -> assertThat(mentor.getNationality()).isEqualTo(ANONYMOUS),
                () -> assertThat(mentor.getProfileImageUrl()).isEqualTo(EMPTY),
                () -> assertThat(mentor.getAvailableLanguages()).isEmpty(),
                () -> assertThat(mentor.getRoleTypes()).containsExactlyInAnyOrder(MENTOR),
                () -> assertThat(mentor.getUniversityProfile().getSchool()).isEqualTo(EMPTY),
                () -> assertThat(mentor.getUniversityProfile().getMajor()).isEqualTo(EMPTY),
                () -> assertThat(mentor.getUniversityProfile().getGrade()).isEqualTo(0),
                () -> assertThat(mentor.getMeetingUrl()).isEqualTo(EMPTY),
                () -> assertThat(mentor.getIntroduction()).isEqualTo(EMPTY),
                () -> assertThat(mentor.getChatTimes()).isEmpty()
        );

        // when
        sut.completeMentor(command, "mentor-profile-url");

        // then
        assertAll(
                () -> assertThat(mentor.getEmail().getValue()).isEqualTo(MENTOR_1.getEmail().getValue()),
                () -> assertThat(mentor.getPassword()).isNotNull(),
                () -> assertThat(mentor.getName()).isEqualTo(command.name()),
                () -> assertThat(mentor.getNationality()).isEqualTo(command.nationality()),
                () -> assertThat(mentor.getProfileImageUrl()).isEqualTo("mentor-profile-url"),
                () -> assertThat(mentor.getLanguages()).containsExactlyInAnyOrderElementsOf(command.languages()),
                () -> assertThat(mentor.getRoleTypes()).containsExactlyInAnyOrder(MENTOR),
                () -> assertThat(mentor.getUniversityProfile().getSchool()).isEqualTo(command.universityProfile().getSchool()),
                () -> assertThat(mentor.getUniversityProfile().getMajor()).isEqualTo(command.universityProfile().getMajor()),
                () -> assertThat(mentor.getUniversityProfile().getGrade()).isEqualTo(command.universityProfile().getGrade()),
                () -> assertThat(mentor.getMeetingUrl()).isEqualTo(command.meetingUrl()),
                () -> assertThat(mentor.getIntroduction()).isEqualTo(command.introduction()),
                () -> assertThat(mentor.getChatTimes()).hasSize(command.schedules().size())
        );
    }

    @Test
    @DisplayName("Mentee 부가 정보를 기입한다")
    void completeMentee() {
        // given
        final Mentee mentee = new Mentee(MENTEE_1.getEmail(), MENTEE_1.getPassword()).apply(1L);
        final CompleteMenteeCommand command = new CompleteMenteeCommand(
                mentee.getId(),
                MENTEE_1.getName(),
                MENTEE_1.getNationality(),
                FileConverter.convertFile(createFile("cat.png", "image/png")),
                MENTEE_1.getLanguages(),
                MENTEE_1.getInterest()
        );
        given(menteeRepository.getById(command.menteeId())).willReturn(mentee);

        assertAll(
                () -> assertThat(mentee.getEmail().getValue()).isEqualTo(MENTEE_1.getEmail().getValue()),
                () -> assertThat(mentee.getPassword()).isNotNull(),
                () -> assertThat(mentee.getName()).isEqualTo(EMPTY),
                () -> assertThat(mentee.getNationality()).isEqualTo(ANONYMOUS),
                () -> assertThat(mentee.getProfileImageUrl()).isEqualTo(EMPTY),
                () -> assertThat(mentee.getAvailableLanguages()).isEmpty(),
                () -> assertThat(mentee.getRoleTypes()).containsExactlyInAnyOrder(MENTEE),
                () -> assertThat(mentee.getInterest().getSchool()).isEqualTo(EMPTY),
                () -> assertThat(mentee.getInterest().getMajor()).isEqualTo(EMPTY)
        );

        // when
        sut.completeMentee(command, "mentee-profile-url");

        // then
        assertAll(
                () -> assertThat(mentee.getEmail().getValue()).isEqualTo(MENTEE_1.getEmail().getValue()),
                () -> assertThat(mentee.getPassword()).isNotNull(),
                () -> assertThat(mentee.getName()).isEqualTo(command.name()),
                () -> assertThat(mentee.getNationality()).isEqualTo(command.nationality()),
                () -> assertThat(mentee.getProfileImageUrl()).isEqualTo("mentee-profile-url"),
                () -> assertThat(mentee.getLanguages()).containsExactlyInAnyOrderElementsOf(command.languages()),
                () -> assertThat(mentee.getRoleTypes()).containsExactlyInAnyOrder(MENTEE),
                () -> assertThat(mentee.getInterest().getSchool()).isEqualTo(command.interest().getSchool()),
                () -> assertThat(mentee.getInterest().getMajor()).isEqualTo(command.interest().getMajor())
        );
    }
}