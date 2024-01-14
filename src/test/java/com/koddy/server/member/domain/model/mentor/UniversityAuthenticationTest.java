package com.koddy.server.member.domain.model.mentor;

import com.koddy.server.common.ParallelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.koddy.server.member.domain.model.mentor.AuthenticationStatus.ATTEMPT;
import static com.koddy.server.member.domain.model.mentor.AuthenticationStatus.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member/Mentor -> 도메인 [UniversityAuthentication] 테스트")
class UniversityAuthenticationTest extends ParallelTest {
    @Test
    @DisplayName("메일로 학교 인증을 시도한다")
    void attemptMail() {
        /* 1. 시도 */
        final String schoolMail = "sjiwon@kyonggi.ac.kr";
        final UniversityAuthentication attemptMail = UniversityAuthentication.attemptMail(schoolMail);

        assertAll(
                () -> assertThat(attemptMail.getSchoolMail()).isEqualTo(schoolMail),
                () -> assertThat(attemptMail.getProofDataUploadUrl()).isNull(),
                () -> assertThat(attemptMail.getStatus()).isEqualTo(ATTEMPT),
                () -> assertThat(attemptMail.isAuthenticated()).isFalse()
        );

        /* 2. 완료 */
        attemptMail.complete();
        assertAll(
                () -> assertThat(attemptMail.getSchoolMail()).isEqualTo(schoolMail),
                () -> assertThat(attemptMail.getProofDataUploadUrl()).isNull(),
                () -> assertThat(attemptMail.getStatus()).isEqualTo(SUCCESS),
                () -> assertThat(attemptMail.isAuthenticated()).isTrue()
        );
    }

    @Test
    @DisplayName("증명 자료로 학교 인증을 시도한다")
    void attemptProofData() {
        /* 1. 시도 */
        final String proofDataUploadUrl = "upload-url";
        final UniversityAuthentication attemptProofData = UniversityAuthentication.attemptProofData(proofDataUploadUrl);

        assertAll(
                () -> assertThat(attemptProofData.getSchoolMail()).isNull(),
                () -> assertThat(attemptProofData.getProofDataUploadUrl()).isEqualTo(proofDataUploadUrl),
                () -> assertThat(attemptProofData.getStatus()).isEqualTo(ATTEMPT),
                () -> assertThat(attemptProofData.isAuthenticated()).isFalse()
        );

        /* 2. 완료 */
        attemptProofData.complete();
        assertAll(
                () -> assertThat(attemptProofData.getSchoolMail()).isNull(),
                () -> assertThat(attemptProofData.getProofDataUploadUrl()).isEqualTo(proofDataUploadUrl),
                () -> assertThat(attemptProofData.getStatus()).isEqualTo(SUCCESS),
                () -> assertThat(attemptProofData.isAuthenticated()).isTrue()
        );
    }
}
