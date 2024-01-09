package com.koddy.server.member.application.usecase;

import com.koddy.server.common.UseCaseTest;
import com.koddy.server.member.application.usecase.query.response.MenteeProfile;
import com.koddy.server.member.application.usecase.query.response.MentorProfile;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.model.mentor.Timeline;
import com.koddy.server.member.domain.repository.MenteeRepository;
import com.koddy.server.member.domain.repository.MentorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.koddy.server.common.fixture.LanguageFixture.EN_SUB;
import static com.koddy.server.common.fixture.LanguageFixture.JP_SUB;
import static com.koddy.server.common.fixture.LanguageFixture.KR_MAIN;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.fixture.TimelineFixture.MON_09_17;
import static com.koddy.server.common.fixture.TimelineFixture.THU_09_17;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@DisplayName("Member -> GetMemberPrivateProfileUseCase 테스트")
class GetMemberPrivateProfileUseCaseTest extends UseCaseTest {
    private final MentorRepository mentorRepository = mock(MentorRepository.class);
    private final MenteeRepository menteeRepository = mock(MenteeRepository.class);
    private final GetMemberPrivateProfileUseCase sut = new GetMemberPrivateProfileUseCase(mentorRepository, menteeRepository);

    @Nested
    @DisplayName("멘토 프로필 조회")
    class GetMentorProfile {
        @Test
        @DisplayName("멘토 프로필을 조회한다 (미완성 - 자기소개, 스케줄)")
        void uncomplete() {
            // given
            final List<Language> languages = List.of(KR_MAIN.toDomain());
            final Mentor mentor = new Mentor(
                    MENTOR_1.getEmail(),
                    MENTOR_1.getName(),
                    MENTOR_1.getProfileImageUrl(),
                    languages,
                    MENTOR_1.getUniversityProfile()
            ).apply(1L);
            given(mentorRepository.getProfile(mentor.getId())).willReturn(mentor);

            // when
            final MentorProfile mentorProfile = sut.getMentorProfile(mentor.getId());

            // then
            assertAll(
                    () -> assertThat(mentorProfile.id()).isEqualTo(mentor.getId()),
                    () -> assertThat(mentorProfile.email()).isEqualTo(mentor.getEmail().getValue()),
                    () -> assertThat(mentorProfile.name()).isEqualTo(mentor.getName()),
                    () -> assertThat(mentorProfile.profileImageUrl()).isEqualTo(mentor.getProfileImageUrl()),
                    () -> assertThat(mentorProfile.nationality()).isEqualTo(mentor.getNationality().getKor()),
                    () -> assertThat(mentorProfile.introduction()).isNull(),
                    () -> assertThat(mentorProfile.languages().mainLanguage()).isEqualTo(KR_MAIN.getCategory().getValue()),
                    () -> assertThat(mentorProfile.languages().subLanguages()).isEmpty(),
                    () -> assertThat(mentorProfile.university().school()).isEqualTo(mentor.getUniversityProfile().getSchool()),
                    () -> assertThat(mentorProfile.university().major()).isEqualTo(mentor.getUniversityProfile().getMajor()),
                    () -> assertThat(mentorProfile.university().enteredIn()).isEqualTo(mentor.getUniversityProfile().getEnteredIn()),
                    () -> assertThat(mentorProfile.schedules()).isEmpty(),
                    () -> assertThat(mentorProfile.profileComplete()).isFalse()
            );
        }

        @Test
        @DisplayName("멘토 프로필을 조회한다 (완성)")
        void complete() {
            // given
            final List<Language> languages = List.of(KR_MAIN.toDomain());
            final List<Timeline> timelines = List.of(MON_09_17.toDomain(), THU_09_17.toDomain());
            final Mentor mentor = MENTOR_1.toDomainWithLanguagesAndTimelines(languages, timelines).apply(1L);
            given(mentorRepository.getProfile(mentor.getId())).willReturn(mentor);

            // when
            final MentorProfile mentorProfile = sut.getMentorProfile(mentor.getId());

            // then
            assertAll(
                    () -> assertThat(mentorProfile.id()).isEqualTo(mentor.getId()),
                    () -> assertThat(mentorProfile.email()).isEqualTo(mentor.getEmail().getValue()),
                    () -> assertThat(mentorProfile.name()).isEqualTo(mentor.getName()),
                    () -> assertThat(mentorProfile.profileImageUrl()).isEqualTo(mentor.getProfileImageUrl()),
                    () -> assertThat(mentorProfile.nationality()).isEqualTo(mentor.getNationality().getKor()),
                    () -> assertThat(mentorProfile.introduction()).isEqualTo(mentor.getIntroduction()),
                    () -> assertThat(mentorProfile.languages().mainLanguage()).isEqualTo(KR_MAIN.getCategory().getValue()),
                    () -> assertThat(mentorProfile.languages().subLanguages()).isEmpty(),
                    () -> assertThat(mentorProfile.university().school()).isEqualTo(mentor.getUniversityProfile().getSchool()),
                    () -> assertThat(mentorProfile.university().major()).isEqualTo(mentor.getUniversityProfile().getMajor()),
                    () -> assertThat(mentorProfile.university().enteredIn()).isEqualTo(mentor.getUniversityProfile().getEnteredIn()),
                    () -> assertThat(mentorProfile.schedules())
                            .usingRecursiveComparison()
                            .isEqualTo(
                                    timelines.stream()
                                            .map(it -> new MentorProfile.ScheduleResponse(
                                                    it.getDayOfWeek().getKor(),
                                                    new MentorProfile.ScheduleResponse.Start(
                                                            it.getPeriod().getStartTime().getHour(),
                                                            it.getPeriod().getStartTime().getMinute()
                                                    ),
                                                    new MentorProfile.ScheduleResponse.End(
                                                            it.getPeriod().getEndTime().getHour(),
                                                            it.getPeriod().getEndTime().getMinute()
                                                    )
                                            ))
                                            .toList()
                            ),
                    () -> assertThat(mentorProfile.profileComplete()).isTrue()
            );
        }
    }

    @Nested
    @DisplayName("멘티 프로필 조회")
    class GetMenteeProfile {
        @Test
        @DisplayName("멘티 프로필을 조회한다 (미완성 - 자기소개)")
        void uncomplete() {
            // given
            final List<Language> languages = List.of(KR_MAIN.toDomain(), EN_SUB.toDomain(), JP_SUB.toDomain());
            final Mentee mentee = new Mentee(
                    MENTEE_1.getEmail(),
                    MENTEE_1.getName(),
                    MENTEE_1.getProfileImageUrl(),
                    MENTEE_1.getNationality(),
                    languages,
                    MENTEE_1.getInterest()
            ).apply(1L);
            given(menteeRepository.getProfile(mentee.getId())).willReturn(mentee);

            // when
            final MenteeProfile menteeProfile = sut.getMenteeProfile(mentee.getId());

            // then
            assertAll(
                    () -> assertThat(menteeProfile.id()).isEqualTo(mentee.getId()),
                    () -> assertThat(menteeProfile.email()).isEqualTo(mentee.getEmail().getValue()),
                    () -> assertThat(menteeProfile.name()).isEqualTo(mentee.getName()),
                    () -> assertThat(menteeProfile.profileImageUrl()).isEqualTo(mentee.getProfileImageUrl()),
                    () -> assertThat(menteeProfile.nationality()).isEqualTo(mentee.getNationality().getKor()),
                    () -> assertThat(menteeProfile.introduction()).isNull(),
                    () -> assertThat(menteeProfile.languages().mainLanguage()).isEqualTo(KR_MAIN.getCategory().getValue()),
                    () -> assertThat(menteeProfile.languages().subLanguages()).containsExactlyInAnyOrder(
                            EN_SUB.getCategory().getValue(),
                            JP_SUB.getCategory().getValue()
                    ),
                    () -> assertThat(menteeProfile.interest().school()).isEqualTo(mentee.getInterest().getSchool()),
                    () -> assertThat(menteeProfile.interest().major()).isEqualTo(mentee.getInterest().getMajor()),
                    () -> assertThat(menteeProfile.profileComplete()).isFalse()
            );
        }

        @Test
        @DisplayName("멘티 프로필을 조회한다 (완성)")
        void complete() {
            // given
            final List<Language> languages = List.of(KR_MAIN.toDomain(), EN_SUB.toDomain(), JP_SUB.toDomain());
            final Mentee mentee = MENTEE_1.toDomainWithLanguages(languages).apply(1L);
            given(menteeRepository.getProfile(mentee.getId())).willReturn(mentee);

            // when
            final MenteeProfile menteeProfile = sut.getMenteeProfile(mentee.getId());

            // then
            assertAll(
                    () -> assertThat(menteeProfile.id()).isEqualTo(mentee.getId()),
                    () -> assertThat(menteeProfile.email()).isEqualTo(mentee.getEmail().getValue()),
                    () -> assertThat(menteeProfile.name()).isEqualTo(mentee.getName()),
                    () -> assertThat(menteeProfile.profileImageUrl()).isEqualTo(mentee.getProfileImageUrl()),
                    () -> assertThat(menteeProfile.nationality()).isEqualTo(mentee.getNationality().getKor()),
                    () -> assertThat(menteeProfile.introduction()).isEqualTo(mentee.getIntroduction()),
                    () -> assertThat(menteeProfile.languages().mainLanguage()).isEqualTo(KR_MAIN.getCategory().getValue()),
                    () -> assertThat(menteeProfile.languages().subLanguages()).containsExactlyInAnyOrder(
                            EN_SUB.getCategory().getValue(),
                            JP_SUB.getCategory().getValue()
                    ),
                    () -> assertThat(menteeProfile.interest().school()).isEqualTo(mentee.getInterest().getSchool()),
                    () -> assertThat(menteeProfile.interest().major()).isEqualTo(mentee.getInterest().getMajor()),
                    () -> assertThat(menteeProfile.profileComplete()).isTrue()
            );
        }
    }
}
