package com.koddy.server.member.application.usecase;

import com.koddy.server.common.IntegrateTest;
import com.koddy.server.common.fixture.MentoringPeriodFixture;
import com.koddy.server.common.fixture.TimelineFixture;
import com.koddy.server.member.application.usecase.command.CompleteMenteeProfileCommand;
import com.koddy.server.member.application.usecase.command.CompleteMentorProfileCommand;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MenteeRepository;
import com.koddy.server.member.domain.repository.MentorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member -> CompleteProfileUseCase 테스트 [IntegrateTest]")
class CompleteProfileUseCaseIntegrateTest extends IntegrateTest {
    @Autowired
    private CompleteProfileUseCase sut;

    @Autowired
    private MentorRepository mentorRepository;

    @Autowired
    private MenteeRepository menteeRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    @DisplayName("멘토 프로필을 완성한다")
    void completeMentor() {
        // given
        final Mentor mentor = mentorRepository.save(new Mentor(
                MENTOR_1.getPlatform(),
                MENTOR_1.getName(),
                MENTOR_1.getProfileImageUrl(),
                MENTOR_1.getLanguages(),
                MENTOR_1.getUniversityProfile()
        ));
        assertAll(
                () -> assertThat(mentor.getIntroduction()).isNull(),
                () -> assertThat(mentor.getMentoringPeriod()).isNull(),
                () -> assertThat(mentor.getSchedules()).isEmpty(),
                () -> assertThat(mentor.isProfileComplete()).isFalse()
        );

        final CompleteMentorProfileCommand command = new CompleteMentorProfileCommand(
                mentor.getId(),
                MENTOR_1.getIntroduction(),
                MentoringPeriodFixture.FROM_03_01_TO_05_01.toDomain(),
                TimelineFixture.주말()
        );

        // when
        sut.completeMentor(command);

        // then
        transactionTemplate.executeWithoutResult(status -> {
            final Mentor findMentor = mentorRepository.findById(mentor.getId()).orElseThrow();
            assertAll(
                    () -> assertThat(findMentor.getIntroduction()).isEqualTo(command.introduction()),
                    () -> assertThat(findMentor.getMentoringPeriod().getStartDate()).isEqualTo(command.mentoringPeriod().getStartDate()),
                    () -> assertThat(findMentor.getMentoringPeriod().getEndDate()).isEqualTo(command.mentoringPeriod().getEndDate()),
                    () -> assertThat(findMentor.getSchedules()).hasSize(command.timelines().size()),
                    () -> assertThat(findMentor.isProfileComplete()).isTrue()
            );
        }); // for schedule mapping
    }

    @Test
    @DisplayName("멘티 프로필을 완성한다")
    void completeMentee() {
        // given
        final Mentee mentee = menteeRepository.save(new Mentee(
                MENTEE_1.getPlatform(),
                MENTEE_1.getName(),
                MENTEE_1.getProfileImageUrl(),
                MENTEE_1.getNationality(),
                MENTEE_1.getLanguages(),
                MENTEE_1.getInterest()
        ));
        assertAll(
                () -> assertThat(mentee.getIntroduction()).isNull(),
                () -> assertThat(mentee.isProfileComplete()).isFalse()
        );

        final CompleteMenteeProfileCommand command = new CompleteMenteeProfileCommand(
                mentee.getId(),
                MENTEE_1.getIntroduction()
        );

        // when
        sut.completeMentee(command);

        // then
        final Mentee findMentee = menteeRepository.findById(mentee.getId()).orElseThrow();
        assertAll(
                () -> assertThat(findMentee.getIntroduction()).isEqualTo(command.introduction()),
                () -> assertThat(findMentee.isProfileComplete()).isTrue()
        );
    }
}
