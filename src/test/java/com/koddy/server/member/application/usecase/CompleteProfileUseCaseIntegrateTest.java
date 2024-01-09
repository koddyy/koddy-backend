package com.koddy.server.member.application.usecase;

import com.koddy.server.common.IntegrateTest;
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
import static com.koddy.server.member.domain.model.ProfileComplete.NO;
import static com.koddy.server.member.domain.model.ProfileComplete.YES;
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
                MENTOR_1.getEmail(),
                MENTOR_1.getName(),
                MENTOR_1.getProfileImageUrl(),
                MENTOR_1.getLanguages(),
                MENTOR_1.getUniversityProfile()
        ));
        assertAll(
                () -> assertThat(mentor.isProfileComplete()).isFalse(),
                () -> assertThat(mentor.getProfileComplete()).isEqualTo(NO)
        );

        // when
        sut.completeMentor(new CompleteMentorProfileCommand(
                mentor.getId(),
                MENTOR_1.getIntroduction(),
                TimelineFixture.주말()
        ));

        // then
        transactionTemplate.executeWithoutResult(status -> {
            final Mentor findMentor = mentorRepository.findById(mentor.getId()).orElseThrow();
            assertAll(
                    () -> assertThat(findMentor.isProfileComplete()).isTrue(),
                    () -> assertThat(findMentor.getProfileComplete()).isEqualTo(YES)
            );
        }); // for schedule mapping
    }

    @Test
    @DisplayName("멘티 프로필을 완성한다")
    void completeMentee() {
        // given
        final Mentee mentee = menteeRepository.save(new Mentee(
                MENTEE_1.getEmail(),
                MENTEE_1.getName(),
                MENTEE_1.getProfileImageUrl(),
                MENTEE_1.getNationality(),
                MENTEE_1.getLanguages(),
                MENTEE_1.getInterest()
        ));
        assertAll(
                () -> assertThat(mentee.isProfileComplete()).isFalse(),
                () -> assertThat(mentee.getProfileComplete()).isEqualTo(NO)
        );

        // when
        sut.completeMentee(new CompleteMenteeProfileCommand(
                mentee.getId(),
                MENTEE_1.getIntroduction()
        ));

        // then
        final Mentee findMentee = menteeRepository.findById(mentee.getId()).orElseThrow();
        assertAll(
                () -> assertThat(findMentee.isProfileComplete()).isTrue(),
                () -> assertThat(findMentee.getProfileComplete()).isEqualTo(YES)
        );
    }
}
