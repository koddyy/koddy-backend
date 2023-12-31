package com.koddy.server.member.domain.model.mentee;

import com.koddy.server.common.ParallelTest;
import com.koddy.server.global.encrypt.Encryptor;
import com.koddy.server.member.exception.MemberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_2;
import static com.koddy.server.common.utils.EncryptorFactory.getEncryptor;
import static com.koddy.server.member.domain.model.Member.EMPTY;
import static com.koddy.server.member.domain.model.Nationality.ANONYMOUS;
import static com.koddy.server.member.domain.model.RoleType.MENTEE;
import static com.koddy.server.member.exception.MemberExceptionCode.CURRENT_PASSWORD_IS_NOT_MATCH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member/Mentee -> 도메인 [Mentee] 테스트")
class MenteeTest extends ParallelTest {
    @Nested
    @DisplayName("Mentee 생성")
    class Construct {
        @Test
        @DisplayName("Mentee를 생성한다")
        void success() {
            /* 초기 Mentee */
            final Mentee mentee = new Mentee(MENTEE_1.getEmail(), MENTEE_1.getPassword());
            assertAll(
                    () -> assertThat(mentee.getEmail().getValue()).isEqualTo(MENTEE_1.getEmail().getValue()),
                    () -> assertThat(mentee.getPassword()).isNotNull(),
                    () -> assertThat(mentee.getName()).isEqualTo(EMPTY),
                    () -> assertThat(mentee.getNationality()).isEqualTo(ANONYMOUS),
                    () -> assertThat(mentee.getProfileImageUrl()).isEqualTo(EMPTY),
                    () -> assertThat(mentee.getIntroduction()).isEqualTo(EMPTY),
                    () -> assertThat(mentee.getAvailableLanguages()).isEmpty(),
                    () -> assertThat(mentee.getRoleTypes()).containsExactlyInAnyOrder(MENTEE),
                    () -> assertThat(mentee.getInterest().getSchool()).isEqualTo(EMPTY),
                    () -> assertThat(mentee.getInterest().getMajor()).isEqualTo(EMPTY),
                    () -> assertThat(mentee.isAuthenticated()).isFalse()
            );

            /* complete Mentee */
            mentee.complete(
                    MENTEE_1.getName(),
                    MENTEE_1.getNationality(),
                    MENTEE_1.getProfileImageUrl(),
                    MENTEE_1.getIntroduction(),
                    MENTEE_1.getLanguages(),
                    MENTEE_1.getInterest()
            );
            assertAll(
                    () -> assertThat(mentee.getEmail().getValue()).isEqualTo(MENTEE_1.getEmail().getValue()),
                    () -> assertThat(mentee.getPassword()).isNotNull(),
                    () -> assertThat(mentee.getName()).isEqualTo(MENTEE_1.getName()),
                    () -> assertThat(mentee.getNationality()).isEqualTo(MENTEE_1.getNationality()),
                    () -> assertThat(mentee.getProfileImageUrl()).isEqualTo(MENTEE_1.getProfileImageUrl()),
                    () -> assertThat(mentee.getIntroduction()).isEqualTo(MENTEE_1.getIntroduction()),
                    () -> assertThat(mentee.getLanguages()).containsExactlyInAnyOrderElementsOf(MENTEE_1.getLanguages()),
                    () -> assertThat(mentee.getRoleTypes()).containsExactlyInAnyOrder(MENTEE),
                    () -> assertThat(mentee.getInterest().getSchool()).isEqualTo(MENTEE_1.getInterest().getSchool()),
                    () -> assertThat(mentee.getInterest().getMajor()).isEqualTo(MENTEE_1.getInterest().getMajor())
            );
        }
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
                    () -> assertThat(mentee.getRoleTypes()).containsExactlyInAnyOrder(MENTEE),
                    () -> assertThat(mentee.getInterest().getSchool()).isEqualTo(MENTEE_2.getInterest().getSchool()),
                    () -> assertThat(mentee.getInterest().getMajor()).isEqualTo(MENTEE_2.getInterest().getMajor())
            );
        }
    }

    @Nested
    @DisplayName("Mentee 비밀번호 수정")
    class UpdatePassword {
        private final Encryptor encryptor = getEncryptor();
        private final String currentPassword = "Koddy123!@#";
        private final String updatePassword = "MenteeUpdate123!@#";

        @Test
        @DisplayName("기존 비밀번호 확인 시 일치하지 않으면 수정할 수 없다")
        void throwExceptionByCurrentPasswordIsNotMatch() {
            // given
            final Mentee mentee = MENTEE_1.toDomain().apply(1L);

            // when - then
            assertThatThrownBy(() -> mentee.updatePassword(currentPassword + "diff", updatePassword, encryptor))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(CURRENT_PASSWORD_IS_NOT_MATCH.getMessage());
        }

        @Test
        @DisplayName("Mentee 비밀번호를 수정한다")
        void success() {
            // given
            final Mentee mentee = MENTEE_1.toDomain().apply(1L);

            // when
            mentee.updatePassword(currentPassword, updatePassword, getEncryptor());

            // then
            assertAll(
                    () -> assertThat(encryptor.isHashMatch(currentPassword, mentee.getPassword().getValue())).isFalse(),
                    () -> assertThat(encryptor.isHashMatch(updatePassword, mentee.getPassword().getValue())).isTrue()
            );
        }
    }
}
