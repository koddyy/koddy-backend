package com.koddy.server.member.domain.model.mentor;

import com.koddy.server.common.ParallelTest;
import com.koddy.server.common.fixture.LanguageFixture;
import com.koddy.server.common.fixture.TimelineFixture;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.Role;
import com.koddy.server.member.exception.MemberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_2;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_3;
import static com.koddy.server.member.domain.model.Nationality.KOREA;
import static com.koddy.server.member.domain.model.ProfileComplete.NO;
import static com.koddy.server.member.domain.model.ProfileComplete.YES;
import static com.koddy.server.member.domain.model.mentor.AuthenticationStatus.ATTEMPT;
import static com.koddy.server.member.domain.model.mentor.AuthenticationStatus.SUCCESS;
import static com.koddy.server.member.exception.MemberExceptionCode.MAIN_LANGUAGE_MUST_BE_ONLY_ONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member/Mentor -> 도메인 [Mentor] 테스트")
class MentorTest extends ParallelTest {
    @Nested
    @DisplayName("Mentor 생성")
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
                    MENTOR_1.getEmail(),
                    MENTOR_1.name(),
                    MENTOR_1.getProfileImageUrl(),
                    languages,
                    MENTOR_1.getUniversityProfile()
            ))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(MAIN_LANGUAGE_MUST_BE_ONLY_ONE.getMessage());
        }

        @Test
        @DisplayName("Mentor를 생성한다")
        void success() {
            final Mentor mentor = MENTOR_1.toDomain();

            assertAll(
                    () -> assertThat(mentor.getEmail().getValue()).isEqualTo(MENTOR_1.getEmail().getValue()),
                    () -> assertThat(mentor.getName()).isEqualTo(MENTOR_1.getName()),
                    () -> assertThat(mentor.getNationality()).isEqualTo(KOREA),
                    () -> assertThat(mentor.getProfileImageUrl()).isEqualTo(MENTOR_1.getProfileImageUrl()),
                    () -> assertThat(mentor.getIntroduction()).isEqualTo(MENTOR_1.getIntroduction()),
                    () -> assertThat(mentor.isActive()).isTrue(),
                    () -> assertThat(mentor.getLanguages()).containsExactlyInAnyOrderElementsOf(MENTOR_1.getLanguages()),
                    () -> assertThat(mentor.getAuthorities()).containsExactlyInAnyOrder(Role.Type.MENTOR.getAuthority()),
                    () -> assertThat(mentor.getUniversityProfile().getSchool()).isEqualTo(MENTOR_1.getUniversityProfile().getSchool()),
                    () -> assertThat(mentor.getUniversityProfile().getMajor()).isEqualTo(MENTOR_1.getUniversityProfile().getMajor()),
                    () -> assertThat(mentor.getUniversityProfile().getEnteredIn()).isEqualTo(MENTOR_1.getUniversityProfile().getEnteredIn()),
                    () -> assertThat(mentor.getSchedules())
                            .map(Schedule::getTimeline)
                            .containsExactlyInAnyOrderElementsOf(MENTOR_1.getTimelines()),
                    () -> assertThat(mentor.isProfileComplete()).isTrue(),
                    () -> assertThat(mentor.getProfileComplete()).isEqualTo(YES)
            );
        }
    }

    @Test
    @DisplayName("Mentor 프로필이 완성되었는지 확인한다 (자기소개, 스케줄)")
    void isProfileComplete() {
        /* mentorA 완성 */
        final Mentor mentorA = MENTOR_1.toDomain();
        assertAll(
                () -> assertThat(mentorA.isProfileComplete()).isTrue(),
                () -> assertThat(mentorA.getProfileComplete()).isEqualTo(YES)
        );

        /* mentorB 1차 회원가입 */
        final Mentor mentorB = new Mentor(
                MENTOR_2.getEmail(),
                MENTOR_2.getName(),
                MENTOR_2.getProfileImageUrl(),
                MENTOR_2.getLanguages(),
                MENTOR_2.getUniversityProfile()
        );
        assertAll(
                () -> assertThat(mentorB.isProfileComplete()).isFalse(),
                () -> assertThat(mentorB.getProfileComplete()).isEqualTo(NO)
        );

        /* mentorB 프로필 완성 */
        mentorB.completeInfo(MENTOR_2.getIntroduction(), TimelineFixture.allDays());
        assertAll(
                () -> assertThat(mentorB.isProfileComplete()).isTrue(),
                () -> assertThat(mentorB.getProfileComplete()).isEqualTo(YES)
        );

        /* mentorC 완성 */
        final Mentor mentorC = MENTOR_3.toDomain();
        assertAll(
                () -> assertThat(mentorC.isProfileComplete()).isTrue(),
                () -> assertThat(mentorC.getProfileComplete()).isEqualTo(YES)
        );

        /* mentorC 미완성 진행 */
        mentorC.completeInfo(null, List.of());
        assertAll(
                () -> assertThat(mentorC.isProfileComplete()).isFalse(),
                () -> assertThat(mentorC.getProfileComplete()).isEqualTo(NO)
        );
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
                    () -> assertThat(mentor.getName()).isEqualTo(MENTOR_2.getName()),
                    () -> assertThat(mentor.getProfileImageUrl()).isEqualTo(MENTOR_2.getProfileImageUrl()),
                    () -> assertThat(mentor.getIntroduction()).isEqualTo(MENTOR_2.getIntroduction()),
                    () -> assertThat(mentor.getLanguages()).containsExactlyInAnyOrderElementsOf(MENTOR_2.getLanguages()),
                    () -> assertThat(mentor.getAuthorities()).containsExactlyInAnyOrder(Role.Type.MENTOR.getAuthority()),
                    () -> assertThat(mentor.getUniversityProfile().getSchool()).isEqualTo(MENTOR_2.getUniversityProfile().getSchool()),
                    () -> assertThat(mentor.getUniversityProfile().getMajor()).isEqualTo(MENTOR_2.getUniversityProfile().getMajor()),
                    () -> assertThat(mentor.getUniversityProfile().getEnteredIn()).isEqualTo(MENTOR_2.getUniversityProfile().getEnteredIn()),
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
            final List<Timeline> update = TimelineFixture.allDays();
            mentor.updateSchedules(update);

            // then
            assertThat(mentor.getSchedules())
                    .map(Schedule::getTimeline)
                    .containsExactlyInAnyOrderElementsOf(update);
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
                    () -> assertThat(mentor.getUniversityAuthentication().getStatus()).isEqualTo(ATTEMPT)
            );

            /* 인증 완료 */
            mentor.authComplete();
            assertAll(
                    () -> assertThat(mentor.getUniversityAuthentication().getSchoolMail()).isEqualTo(schoolMail),
                    () -> assertThat(mentor.getUniversityAuthentication().getProofDataUploadUrl()).isNull(),
                    () -> assertThat(mentor.getUniversityAuthentication().getStatus()).isEqualTo(SUCCESS)
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
                    () -> assertThat(mentor.getUniversityAuthentication().getStatus()).isEqualTo(ATTEMPT)
            );

            /* 인증 완료 */
            mentor.authComplete();
            assertAll(
                    () -> assertThat(mentor.getUniversityAuthentication().getSchoolMail()).isNull(),
                    () -> assertThat(mentor.getUniversityAuthentication().getProofDataUploadUrl()).isEqualTo(proofDataUploadUrl),
                    () -> assertThat(mentor.getUniversityAuthentication().getStatus()).isEqualTo(SUCCESS)
            );
        }
    }
}
