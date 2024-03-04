package com.koddy.server.member.domain.model.mentor

import io.kotest.assertions.assertSoftly
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

@DisplayName("Member/Mentor -> 도메인 [UniversityAuthentication] 테스트")
internal class UniversityAuthenticationTest : FeatureSpec({
    feature("UniversityAuthentication's attemptMail") {
        scenario("메일로 학교 인증을 시도한다") {
            /* 1. 시도 */
            val schoolMail = "sjiwon@kyonggi.ac.kr"
            val attemptMail: UniversityAuthentication = UniversityAuthentication.attemptMail(schoolMail)
            assertSoftly {
                attemptMail.schoolMail shouldBe schoolMail
                attemptMail.proofDataUploadUrl shouldBe null
                attemptMail.status shouldBe UniversityAuthentication.AuthenticationStatus.ATTEMPT
                attemptMail.isAuthenticated shouldBe false
            }

            /* 2. 완료 */
            val completed: UniversityAuthentication = attemptMail.complete()
            assertSoftly {
                completed.schoolMail shouldBe schoolMail
                completed.proofDataUploadUrl shouldBe null
                completed.status shouldBe UniversityAuthentication.AuthenticationStatus.SUCCESS
                completed.isAuthenticated shouldBe true
            }
        }
    }

    feature("UniversityAuthentication's attemptProofData") {
        scenario("증명자료로 학교 인증을 시도한다") {
            /* 1. 시도 */
            val proofDataUploadUrl = "upload-url"
            val attemptProofData: UniversityAuthentication = UniversityAuthentication.attemptProofData(proofDataUploadUrl)
            assertSoftly {
                attemptProofData.schoolMail shouldBe null
                attemptProofData.proofDataUploadUrl shouldBe proofDataUploadUrl
                attemptProofData.status shouldBe UniversityAuthentication.AuthenticationStatus.ATTEMPT
                attemptProofData.isAuthenticated shouldBe false
            }

            /* 2. 완료 */
            val completed: UniversityAuthentication = attemptProofData.complete()
            assertSoftly {
                completed.schoolMail shouldBe null
                completed.proofDataUploadUrl shouldBe proofDataUploadUrl
                completed.status shouldBe UniversityAuthentication.AuthenticationStatus.SUCCESS
                completed.isAuthenticated shouldBe true
            }
        }
    }
})
