package com.koddy.server.member.domain.model.mentee;

import com.koddy.server.common.ParallelTest;
import com.koddy.server.common.fixture.LanguageFixture;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.Role;
import com.koddy.server.member.exception.MemberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_2;
import static com.koddy.server.member.domain.model.ProfileComplete.NO;
import static com.koddy.server.member.domain.model.ProfileComplete.YES;
import static com.koddy.server.member.exception.MemberExceptionCode.MAIN_LANGUAGE_MUST_BE_ONLY_ONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member/Mentee -> 도메인 [Mentee] 테스트")
class MenteeTest extends ParallelTest {
    @Nested
    @DisplayName("Mentee 생성")
    class Construct {
        @Test
        @DisplayName("사용 가능한 메인 언어가 1개가 아니면(0 or N) 정책에 의거해서 Mentee를 생성할 수 없다")
        void throwExceptionByMainLanguageIsNotOne() {
            // given
            final List<Language> languages = List.of(
                    LanguageFixture.KR_MAIN.toDomain(),
                    LanguageFixture.EN_MAIN.toDomain(),
                    LanguageFixture.JP_SUB.toDomain()
            );

            // when - then
            assertThatThrownBy(() -> new Mentee(
                    MENTEE_1.getEmail(),
                    MENTEE_1.name(),
                    MENTEE_1.getProfileImageUrl(),
                    MENTEE_1.getNationality(),
                    languages,
                    MENTEE_1.getInterest()
            ))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(MAIN_LANGUAGE_MUST_BE_ONLY_ONE.getMessage());
        }

        @Test
        @DisplayName("Mentee를 생성한다")
        void success() {
            final Mentee mentee = MENTEE_1.toDomain();

            assertAll(
                    () -> assertThat(mentee.getEmail().getValue()).isEqualTo(MENTEE_1.getEmail().getValue()),
                    () -> assertThat(mentee.getName()).isEqualTo(MENTEE_1.getName()),
                    () -> assertThat(mentee.getNationality()).isEqualTo(MENTEE_1.getNationality()),
                    () -> assertThat(mentee.getProfileImageUrl()).isEqualTo(MENTEE_1.getProfileImageUrl()),
                    () -> assertThat(mentee.getIntroduction()).isEqualTo(MENTEE_1.getIntroduction()),
                    () -> assertThat(mentee.isActive()).isTrue(),
                    () -> assertThat(mentee.getLanguages()).containsExactlyInAnyOrderElementsOf(MENTEE_1.getLanguages()),
                    () -> assertThat(mentee.getAuthorities()).containsExactlyInAnyOrder(Role.Type.MENTEE.getAuthority()),
                    () -> assertThat(mentee.getInterest().getSchool()).isEqualTo(MENTEE_1.getInterest().getSchool()),
                    () -> assertThat(mentee.getInterest().getMajor()).isEqualTo(MENTEE_1.getInterest().getMajor()),
                    () -> assertThat(mentee.isProfileComplete()).isTrue(),
                    () -> assertThat(mentee.getProfileComplete()).isEqualTo(YES)
            );
        }
    }

    @Test
    @DisplayName("Mentee 프로필이 완성되었는지 확인한다 (자기소개)")
    void isProfileComplete() {
        /* memberA 완성 */
        final Mentee menteeA = MENTEE_1.toDomain();
        assertAll(
                () -> assertThat(menteeA.isProfileComplete()).isTrue(),
                () -> assertThat(menteeA.getProfileComplete()).isEqualTo(YES)
        );

        /* memberB 1차 회원가입 */
        final Mentee menteeB = new Mentee(
                MENTEE_2.getEmail(),
                MENTEE_2.getName(),
                MENTEE_2.getProfileImageUrl(),
                MENTEE_2.getNationality(),
                MENTEE_2.getLanguages(),
                MENTEE_2.getInterest()
        );
        assertAll(
                () -> assertThat(menteeB.isProfileComplete()).isFalse(),
                () -> assertThat(menteeB.getProfileComplete()).isEqualTo(NO)
        );

        /* memberB 프로필 완성 */
        menteeB.completeInfo(MENTEE_2.getIntroduction());
        assertAll(
                () -> assertThat(menteeB.isProfileComplete()).isTrue(),
                () -> assertThat(menteeB.getProfileComplete()).isEqualTo(YES)
        );
    }

    @Nested
    @DisplayName("Mentee 기본 정보 수정")
    class UpdateBasicInfo {
        @Test
        @DisplayName("Mentee 기본 정보를 수정한다")
        void success() {
            // given
            final Mentee mentee = MENTEE_1.toDomain().apply(1L);

            // when
            mentee.updateBasicInfo(
                    MENTEE_2.getName(),
                    MENTEE_2.getNationality(),
                    MENTEE_2.getProfileImageUrl(),
                    MENTEE_2.getIntroduction(),
                    MENTEE_2.getLanguages(),
                    MENTEE_2.getInterest().getSchool(),
                    MENTEE_2.getInterest().getMajor()
            );

            // then
            assertAll(
                    () -> assertThat(mentee.getName()).isEqualTo(MENTEE_2.getName()),
                    () -> assertThat(mentee.getNationality()).isEqualTo(MENTEE_2.getNationality()),
                    () -> assertThat(mentee.getProfileImageUrl()).isEqualTo(MENTEE_2.getProfileImageUrl()),
                    () -> assertThat(mentee.getIntroduction()).isEqualTo(MENTEE_2.getIntroduction()),
                    () -> assertThat(mentee.getLanguages()).containsExactlyInAnyOrderElementsOf(MENTEE_2.getLanguages()),
                    () -> assertThat(mentee.getAuthorities()).containsExactlyInAnyOrder(Role.Type.MENTEE.getAuthority()),
                    () -> assertThat(mentee.getInterest().getSchool()).isEqualTo(MENTEE_2.getInterest().getSchool()),
                    () -> assertThat(mentee.getInterest().getMajor()).isEqualTo(MENTEE_2.getInterest().getMajor())
            );
        }
    }
}
