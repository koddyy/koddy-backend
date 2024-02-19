package com.koddy.server.member.domain.model.mentor;

import com.koddy.server.coffeechat.domain.model.Reservation;
import com.koddy.server.common.UnitTest;
import com.koddy.server.common.fixture.LanguageFixture;
import com.koddy.server.common.fixture.MentoringPeriodFixture;
import com.koddy.server.common.fixture.TimelineFixture;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.exception.MemberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_2;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_3;
import static com.koddy.server.member.domain.model.Member.Status.ACTIVE;
import static com.koddy.server.member.domain.model.Nationality.KOREA;
import static com.koddy.server.member.domain.model.Role.MENTOR;
import static com.koddy.server.member.domain.model.mentor.AuthenticationStatus.ATTEMPT;
import static com.koddy.server.member.domain.model.mentor.AuthenticationStatus.SUCCESS;
import static com.koddy.server.member.domain.model.mentor.DayOfWeek.FRI;
import static com.koddy.server.member.domain.model.mentor.DayOfWeek.THU;
import static com.koddy.server.member.domain.model.mentor.DayOfWeek.TUE;
import static com.koddy.server.member.domain.model.mentor.DayOfWeek.WED;
import static com.koddy.server.member.exception.MemberExceptionCode.CANNOT_RESERVATION;
import static com.koddy.server.member.exception.MemberExceptionCode.MAIN_LANGUAGE_MUST_BE_ONLY_ONE;
import static com.koddy.server.member.exception.MemberExceptionCode.MENTOR_NOT_FILL_IN_SCHEDULE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Member/Mentor -> 도메인 Aggregate [Mentor] 테스트")
class MentorTest extends UnitTest {
    @Nested
    @DisplayName("초기 Mentor 생성")
    class Construct {
        @Test
        @DisplayName("사용 가능한 메인 언어가 1개가 아니면(0 or N) 정책에 의거해서 Mentor를 생성할 수 없다")
        void throwExceptionByMainLanguageIsNotOne() {
            // given
            final List<Language> languages = List.of(
                    LanguageFixture.KR_MAIN.toDomain(),
                    LanguageFixture.EN_MAIN.toDomain(),
                    LanguageFixture.JP_SUB.toDomain()
            );

            // when - then
            assertThatThrownBy(() -> new Mentor(
                    MENTOR_1.getPlatform(),
                    MENTOR_1.name(),
                    languages,
                    MENTOR_1.getUniversityProfile()
            ))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(MAIN_LANGUAGE_MUST_BE_ONLY_ONE.getMessage());
        }

        @Test
        @DisplayName("초기 Mentor를 생성한다")
        void success() {
            // given
            final Mentor mentor = new Mentor(
                    MENTOR_1.getPlatform(),
                    MENTOR_1.getName(),
                    MENTOR_1.getLanguages(),
                    MENTOR_1.getUniversityProfile()
            );

            // when - then
            assertAll(
                    // Required
                    () -> assertThat(mentor.getPlatform().getProvider()).isEqualTo(MENTOR_1.getPlatform().getProvider()),
                    () -> assertThat(mentor.getPlatform().getSocialId()).isEqualTo(MENTOR_1.getPlatform().getSocialId()),
                    () -> assertThat(mentor.getPlatform().getEmail().getValue()).isEqualTo(MENTOR_1.getPlatform().getEmail().getValue()),
                    () -> assertThat(mentor.getName()).isEqualTo(MENTOR_1.getName()),
                    () -> assertThat(mentor.getNationality()).isEqualTo(KOREA),
                    () -> assertThat(mentor.getStatus()).isEqualTo(ACTIVE),
                    () -> assertThat(mentor.getLanguages()).containsExactlyInAnyOrderElementsOf(MENTOR_1.getLanguages()),
                    () -> assertThat(mentor.getRole()).isEqualTo(MENTOR),
                    () -> assertThat(mentor.getUniversityProfile().getSchool()).isEqualTo(MENTOR_1.getUniversityProfile().getSchool()),
                    () -> assertThat(mentor.getUniversityProfile().getMajor()).isEqualTo(MENTOR_1.getUniversityProfile().getMajor()),
                    () -> assertThat(mentor.getUniversityProfile().getEnteredIn()).isEqualTo(MENTOR_1.getUniversityProfile().getEnteredIn()),

                    // Optional
                    () -> assertThat(mentor.getIntroduction()).isNull(),
                    () -> assertThat(mentor.getProfileImageUrl()).isNull(),
                    () -> assertThat(mentor.getUniversityAuthentication()).isNull(),
                    () -> assertThat(mentor.getMentoringPeriod()).isNull(),
                    () -> assertThat(mentor.getSchedules()).isEmpty(),

                    // isCompleted
                    () -> assertThat(mentor.isProfileComplete()).isFalse()
            );
        }
    }

    @Test
    @DisplayName("Mentor 프로필이 완성되었는지 확인한다 [자기소개 & 프로필 이미지 & 멘토링 기간 & 스케줄]")
    void isProfileComplete() {
        /* mentorA 완성 */
        final Mentor mentorA = MENTOR_1.toDomain();
        assertThat(mentorA.isProfileComplete()).isTrue();

        /* mentorB 1차 회원가입 */
        final Mentor mentorB = new Mentor(
                MENTOR_2.getPlatform(),
                MENTOR_2.getName(),
                MENTOR_2.getLanguages(),
                MENTOR_2.getUniversityProfile()
        );
        assertThat(mentorB.isProfileComplete()).isFalse();

        /* mentorB 프로필 완성 */
        mentorB.completeInfo(
                MENTOR_2.getIntroduction(),
                MENTOR_2.getProfileImageUrl(),
                MentoringPeriodFixture.FROM_03_01_TO_05_01.toDomain(),
                TimelineFixture.allDays()
        );
        assertThat(mentorB.isProfileComplete()).isTrue();

        /* mentorC 1차 회원가입 */
        final Mentor mentorC = new Mentor(
                MENTOR_3.getPlatform(),
                MENTOR_3.getName(),
                MENTOR_3.getLanguages(),
                MENTOR_3.getUniversityProfile()
        );
        assertThat(mentorC.isProfileComplete()).isFalse();

        /* mentorC 자기소개 기입 */
        mentorC.completeInfo(
                MENTOR_3.getIntroduction(),
                MENTOR_3.getProfileImageUrl(),
                null,
                List.of()
        );
        assertThat(mentorC.isProfileComplete()).isFalse();

        /* mentorC 멘토링 스케줄 */
        mentorC.completeInfo(
                MENTOR_3.getIntroduction(),
                MENTOR_3.getProfileImageUrl(),
                MentoringPeriodFixture.FROM_03_01_TO_05_01.toDomain(),
                TimelineFixture.allDays()
        );
        assertThat(mentorC.isProfileComplete()).isTrue();
    }

    @Nested
    @DisplayName("Mentor 기본 정보 수정")
    class UpdateBasicInfo {
        @Test
        @DisplayName("Mentor 기본 정보를 수정한다")
        void success() {
            // given
            final Mentor mentor = MENTOR_1.toDomain().apply(1L);

            // when
            mentor.updateBasicInfo(
                    MENTOR_2.getName(),
                    MENTOR_2.getProfileImageUrl(),
                    MENTOR_2.getIntroduction(),
                    MENTOR_2.getLanguages(),
                    MENTOR_2.getUniversityProfile().getSchool(),
                    MENTOR_2.getUniversityProfile().getMajor(),
                    MENTOR_2.getUniversityProfile().getEnteredIn()
            );

            // then
            assertAll(
                    // Target
                    () -> assertThat(mentor.getName()).isEqualTo(MENTOR_2.getName()),
                    () -> assertThat(mentor.getProfileImageUrl()).isEqualTo(MENTOR_2.getProfileImageUrl()),
                    () -> assertThat(mentor.getIntroduction()).isEqualTo(MENTOR_2.getIntroduction()),
                    () -> assertThat(mentor.getLanguages()).containsExactlyInAnyOrderElementsOf(MENTOR_2.getLanguages()),
                    () -> assertThat(mentor.getUniversityProfile().getSchool()).isEqualTo(MENTOR_2.getUniversityProfile().getSchool()),
                    () -> assertThat(mentor.getUniversityProfile().getMajor()).isEqualTo(MENTOR_2.getUniversityProfile().getMajor()),
                    () -> assertThat(mentor.getUniversityProfile().getEnteredIn()).isEqualTo(MENTOR_2.getUniversityProfile().getEnteredIn()),

                    // Non-Target
                    () -> assertThat(mentor.getMentoringPeriod()).isEqualTo(MENTOR_1.getMentoringPeriod()),
                    () -> assertThat(mentor.getSchedules())
                            .map(Schedule::getTimeline)
                            .containsExactlyInAnyOrderElementsOf(MENTOR_1.getTimelines())
            );
        }
    }

    @Nested
    @DisplayName("Mentor 스케줄 수정")
    class UpdateSchedule {
        @Test
        @DisplayName("Mentor 스케줄을 수정한다")
        void success() {
            // given
            final Mentor mentor = MENTOR_1.toDomain().apply(1L);

            // when
            final MentoringPeriod mentoringPeriod = MentoringPeriodFixture.FROM_03_01_TO_05_01.toDomain();
            final List<Timeline> timelines = TimelineFixture.allDays();
            mentor.updateSchedules(mentoringPeriod, timelines);

            // then
            assertAll(
                    () -> assertThat(mentor.getMentoringPeriod()).isEqualTo(mentoringPeriod),
                    () -> assertThat(mentor.getSchedules())
                            .map(Schedule::getTimeline)
                            .containsExactlyInAnyOrderElementsOf(timelines)
            );
        }
    }

    @Nested
    @DisplayName("멘토 학교 인증")
    class UniversityAuthentication {
        @Test
        @DisplayName("학교 메일로 인증을 진행한다")
        void authWithMail() {
            // given
            final Mentor mentor = MENTOR_1.toDomain().apply(1L);
            final String schoolMail = "sjiwon@kyonggi.ac.kr";

            /* 인증 시도 */
            mentor.authWithMail(schoolMail);
            assertAll(
                    () -> assertThat(mentor.getUniversityAuthentication().getSchoolMail()).isEqualTo(schoolMail),
                    () -> assertThat(mentor.getUniversityAuthentication().getProofDataUploadUrl()).isNull(),
                    () -> assertThat(mentor.getUniversityAuthentication().getStatus()).isEqualTo(ATTEMPT),
                    () -> assertThat(mentor.isAuthenticated()).isFalse()
            );

            /* 인증 완료 */
            mentor.authComplete();
            assertAll(
                    () -> assertThat(mentor.getUniversityAuthentication().getSchoolMail()).isEqualTo(schoolMail),
                    () -> assertThat(mentor.getUniversityAuthentication().getProofDataUploadUrl()).isNull(),
                    () -> assertThat(mentor.getUniversityAuthentication().getStatus()).isEqualTo(SUCCESS),
                    () -> assertThat(mentor.isAuthenticated()).isTrue()
            );
        }

        @Test
        @DisplayName("증명 자료로 인증을 진행한다")
        void authWithProofData() {
            // given
            final Mentor mentor = MENTOR_1.toDomain().apply(1L);
            final String proofDataUploadUrl = "upload-url";

            /* 인증 시도 */
            mentor.authWithProofData(proofDataUploadUrl);
            assertAll(
                    () -> assertThat(mentor.getUniversityAuthentication().getSchoolMail()).isNull(),
                    () -> assertThat(mentor.getUniversityAuthentication().getProofDataUploadUrl()).isEqualTo(proofDataUploadUrl),
                    () -> assertThat(mentor.getUniversityAuthentication().getStatus()).isEqualTo(ATTEMPT),
                    () -> assertThat(mentor.isAuthenticated()).isFalse()
            );

            /* 인증 완료 */
            mentor.authComplete();
            assertAll(
                    () -> assertThat(mentor.getUniversityAuthentication().getSchoolMail()).isNull(),
                    () -> assertThat(mentor.getUniversityAuthentication().getProofDataUploadUrl()).isEqualTo(proofDataUploadUrl),
                    () -> assertThat(mentor.getUniversityAuthentication().getStatus()).isEqualTo(SUCCESS),
                    () -> assertThat(mentor.isAuthenticated()).isTrue()
            );
        }
    }

    @Nested
    @DisplayName("멘토링 예약 날짜 검증")
    class ValidateReservationData {
        @Test
        @DisplayName("멘토가 멘토링 관련 정보를 기입하지 않았다면 검증에 실패하고 예외가 발생한다")
        void throwExceptionByNotCompleted() {
            // given
            final Mentor mentor = new Mentor(
                    MENTOR_1.getPlatform(),
                    MENTOR_1.getName(),
                    MENTOR_1.getLanguages(),
                    MENTOR_1.getUniversityProfile()
            );

            // when - then
            final LocalDateTime target = LocalDateTime.of(2024, 2, 5, 18, 0);
            assertThatThrownBy(() -> mentor.validateReservationData(Reservation.of(target, target.plusMinutes(30))))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(MENTOR_NOT_FILL_IN_SCHEDULE.getMessage());
        }

        @Test
        @DisplayName("예약 날짜가 멘토링 진행 기간에 포함되지 않으면 예외가 발생한다 -> MentoringPeriod[startDate & endDate]")
        void throwExceptionByOutOfDate() {
            // given
            final MentoringPeriod mentoringPeriod = MentoringPeriod.of(
                    LocalDate.of(2024, 2, 6),
                    LocalDate.of(2024, 3, 1)
            );
            final Mentor mentor = MENTOR_1.toDomainWithMentoringInfo(mentoringPeriod, MENTOR_1.getTimelines());

            // when - then
            final LocalDateTime target1 = LocalDateTime.of(2024, 2, 5, 18, 0);
            final LocalDateTime target2 = LocalDateTime.of(2024, 3, 2, 18, 0);

            assertAll(
                    () -> assertThatThrownBy(() -> mentor.validateReservationData(Reservation.of(target1, target1.plusMinutes(30))))
                            .isInstanceOf(MemberException.class)
                            .hasMessage(CANNOT_RESERVATION.getMessage()),
                    () -> assertThatThrownBy(() -> mentor.validateReservationData(Reservation.of(target2, target2.plusMinutes(30))))
                            .isInstanceOf(MemberException.class)
                            .hasMessage(CANNOT_RESERVATION.getMessage())
            );
        }

        @Test
        @DisplayName("멘토링 진행 시간이 멘토가 정한 TimeUnit이랑 일치하지 않으면 예외가 발생한다 -> MentoringPeriod[TimeUnit]")
        void throwExceptionByNotAllowedTimeUnit() {
            // given
            final MentoringPeriod mentoringPeriod = MentoringPeriod.of(
                    LocalDate.of(2024, 2, 6),
                    LocalDate.of(2024, 3, 1)
            );
            final Mentor mentor = MENTOR_1.toDomainWithMentoringInfo(mentoringPeriod, MENTOR_1.getTimelines()); // TODO default = 30

            // when - then
            final LocalDateTime start = LocalDateTime.of(2024, 2, 5, 18, 0);

            assertAll(
                    () -> assertThatThrownBy(() -> mentor.validateReservationData(Reservation.of(start, start.plusMinutes(10))))
                            .isInstanceOf(MemberException.class)
                            .hasMessage(CANNOT_RESERVATION.getMessage()),
                    () -> assertThatThrownBy(() -> mentor.validateReservationData(Reservation.of(start, start.plusMinutes(20))))
                            .isInstanceOf(MemberException.class)
                            .hasMessage(CANNOT_RESERVATION.getMessage()),
                    () -> assertThatThrownBy(() -> mentor.validateReservationData(Reservation.of(start, start.plusMinutes(29))))
                            .isInstanceOf(MemberException.class)
                            .hasMessage(CANNOT_RESERVATION.getMessage()),
                    () -> assertThatThrownBy(() -> mentor.validateReservationData(Reservation.of(start, start.plusMinutes(31))))
                            .isInstanceOf(MemberException.class)
                            .hasMessage(CANNOT_RESERVATION.getMessage()),
                    () -> assertThatThrownBy(() -> mentor.validateReservationData(Reservation.of(start, start.plusMinutes(40))))
                            .isInstanceOf(MemberException.class)
                            .hasMessage(CANNOT_RESERVATION.getMessage())
            );
        }

        @Test
        @DisplayName("예약 날짜가 멘토링 가능 시간에 포함되지 않으면 예외가 발생한다 -> Mentor-Schedule-Timeline")
        void throwExceptionByNotAllowedSchedule() {
            // given
            final MentoringPeriod mentoringPeriod = MentoringPeriod.of(
                    LocalDate.of(2024, 2, 1),
                    LocalDate.of(2024, 3, 1)
            );
            final LocalTime time = LocalTime.of(19, 0);
            final List<Timeline> timelines = List.of(
                    Timeline.of(TUE, time, time.plusHours(3)),
                    Timeline.of(WED, time, time.plusHours(3)),
                    Timeline.of(THU, time, time.plusHours(3)),
                    Timeline.of(FRI, time, time.plusHours(3))
            );
            final Mentor mentor = MENTOR_1.toDomainWithMentoringInfo(mentoringPeriod, timelines);

            // when - then
            final LocalDateTime target1 = LocalDateTime.of(2024, 2, 5, 18, 0);
            final LocalDateTime target2 = LocalDateTime.of(2024, 2, 5, 18, 30);
            final LocalDateTime target3 = LocalDateTime.of(2024, 2, 5, 18, 50);
            final LocalDateTime target4 = LocalDateTime.of(2024, 2, 5, 19, 30);
            final LocalDateTime target5 = LocalDateTime.of(2024, 2, 5, 21, 50);
            final LocalDateTime target6 = LocalDateTime.of(2024, 2, 5, 22, 0);
            final LocalDateTime target7 = LocalDateTime.of(2024, 2, 5, 22, 30);

            assertAll(
                    () -> assertThatThrownBy(() -> mentor.validateReservationData(Reservation.of(target1, target1.plusMinutes(30))))
                            .isInstanceOf(MemberException.class)
                            .hasMessage(CANNOT_RESERVATION.getMessage()),
                    () -> assertThatThrownBy(() -> mentor.validateReservationData(Reservation.of(target2, target2.plusMinutes(30))))
                            .isInstanceOf(MemberException.class)
                            .hasMessage(CANNOT_RESERVATION.getMessage()),
                    () -> assertThatThrownBy(() -> mentor.validateReservationData(Reservation.of(target3, target3.plusMinutes(30))))
                            .isInstanceOf(MemberException.class)
                            .hasMessage(CANNOT_RESERVATION.getMessage()),
                    () -> assertThatThrownBy(() -> mentor.validateReservationData(Reservation.of(target4, target4.plusHours(3))))
                            .isInstanceOf(MemberException.class)
                            .hasMessage(CANNOT_RESERVATION.getMessage()),
                    () -> assertThatThrownBy(() -> mentor.validateReservationData(Reservation.of(target5, target5.plusMinutes(30))))
                            .isInstanceOf(MemberException.class)
                            .hasMessage(CANNOT_RESERVATION.getMessage()),
                    () -> assertThatThrownBy(() -> mentor.validateReservationData(Reservation.of(target6, target6.plusMinutes(30))))
                            .isInstanceOf(MemberException.class)
                            .hasMessage(CANNOT_RESERVATION.getMessage()),
                    () -> assertThatThrownBy(() -> mentor.validateReservationData(Reservation.of(target7, target7.plusMinutes(30))))
                            .isInstanceOf(MemberException.class)
                            .hasMessage(CANNOT_RESERVATION.getMessage())
            );
        }

        @Test
        @DisplayName("멘토의 멘토링 시간대에 대한 검증에 성공한다")
        void success() {
            // given
            final MentoringPeriod mentoringPeriod = MentoringPeriod.of(
                    LocalDate.of(2024, 2, 1),
                    LocalDate.of(2024, 3, 1)
            );
            final LocalTime time = LocalTime.of(18, 0);
            final List<Timeline> timelines = List.of(Timeline.of(FRI, time, time.plusHours(3)));
            final Mentor mentor = MENTOR_1.toDomainWithMentoringInfo(mentoringPeriod, timelines);

            // when - then
            final LocalDateTime target1 = LocalDateTime.of(2024, 2, 2, 18, 0);
            final LocalDateTime target2 = LocalDateTime.of(2024, 2, 2, 18, 30);
            final LocalDateTime target3 = LocalDateTime.of(2024, 2, 2, 19, 0);
            final LocalDateTime target4 = LocalDateTime.of(2024, 2, 2, 19, 30);
            final LocalDateTime target5 = LocalDateTime.of(2024, 2, 2, 20, 0);
            final LocalDateTime target6 = LocalDateTime.of(2024, 2, 2, 20, 30);

            final LocalDateTime target7 = LocalDateTime.of(2024, 3, 1, 18, 0);
            final LocalDateTime target8 = LocalDateTime.of(2024, 3, 1, 18, 30);
            final LocalDateTime target9 = LocalDateTime.of(2024, 3, 1, 19, 0);
            final LocalDateTime target10 = LocalDateTime.of(2024, 3, 1, 19, 30);
            final LocalDateTime target11 = LocalDateTime.of(2024, 3, 1, 20, 0);
            final LocalDateTime target12 = LocalDateTime.of(2024, 3, 1, 20, 30);

            assertAll(
                    () -> assertDoesNotThrow(() -> mentor.validateReservationData(Reservation.of(target1, target1.plusMinutes(30)))),
                    () -> assertDoesNotThrow(() -> mentor.validateReservationData(Reservation.of(target2, target2.plusMinutes(30)))),
                    () -> assertDoesNotThrow(() -> mentor.validateReservationData(Reservation.of(target3, target3.plusMinutes(30)))),
                    () -> assertDoesNotThrow(() -> mentor.validateReservationData(Reservation.of(target4, target4.plusMinutes(30)))),
                    () -> assertDoesNotThrow(() -> mentor.validateReservationData(Reservation.of(target5, target5.plusMinutes(30)))),
                    () -> assertDoesNotThrow(() -> mentor.validateReservationData(Reservation.of(target6, target6.plusMinutes(30)))),
                    () -> assertDoesNotThrow(() -> mentor.validateReservationData(Reservation.of(target7, target7.plusMinutes(30)))),
                    () -> assertDoesNotThrow(() -> mentor.validateReservationData(Reservation.of(target8, target8.plusMinutes(30)))),
                    () -> assertDoesNotThrow(() -> mentor.validateReservationData(Reservation.of(target9, target9.plusMinutes(30)))),
                    () -> assertDoesNotThrow(() -> mentor.validateReservationData(Reservation.of(target10, target10.plusMinutes(30)))),
                    () -> assertDoesNotThrow(() -> mentor.validateReservationData(Reservation.of(target11, target11.plusMinutes(30)))),
                    () -> assertDoesNotThrow(() -> mentor.validateReservationData(Reservation.of(target12, target12.plusMinutes(30))))
            );
        }
    }
}
