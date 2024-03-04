package com.koddy.server.member.domain.model.mentee

import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.fixture.LanguageFixture
import com.koddy.server.common.fixture.MenteeFixtureStore.menteeFixture
import com.koddy.server.member.domain.model.Member
import com.koddy.server.member.domain.model.Role
import com.koddy.server.member.exception.MemberException
import com.koddy.server.member.exception.MemberExceptionCode.AVAILABLE_LANGUAGE_MUST_EXISTS
import com.koddy.server.member.exception.MemberExceptionCode.MAIN_LANGUAGE_MUST_BE_ONLY_ONE
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage

@UnitTestKt
@DisplayName("Member/Mentee -> 도메인 Aggregate [Mentee] 테스트")
internal class MenteeTest : FeatureSpec({
    feature("Mentee 생성") {
        val fixture = menteeFixture(sequence = 1)

        scenario("메인 언어를 (0..N)개 선택했다면 Mentee를 생성할 수 없다") {
            shouldThrow<MemberException> {
                Mentee(
                    platform = fixture.platform,
                    name = fixture.name,
                    nationality = fixture.nationality,
                    languages = listOf(),
                    interest = fixture.interest,
                )
            } shouldHaveMessage AVAILABLE_LANGUAGE_MUST_EXISTS.message

            shouldThrow<MemberException> {
                Mentee(
                    platform = fixture.platform,
                    name = fixture.name,
                    nationality = fixture.nationality,
                    languages = listOf(
                        LanguageFixture.KR_MAIN.toDomain(),
                        LanguageFixture.EN_MAIN.toDomain(),
                        LanguageFixture.JP_SUB.toDomain(),
                    ),
                    interest = fixture.interest,
                )
            } shouldHaveMessage MAIN_LANGUAGE_MUST_BE_ONLY_ONE.message
        }

        scenario("메인 언어를 정확히 1개 선택했다면 Mentee를 생성할 수 있다") {
            listOf(
                listOf(LanguageFixture.KR_MAIN.toDomain()),
                listOf(
                    LanguageFixture.KR_MAIN.toDomain(),
                    LanguageFixture.EN_SUB.toDomain(),
                    LanguageFixture.JP_SUB.toDomain(),
                ),
            ).forEach { language ->
                val result = Mentee(
                    platform = fixture.platform,
                    name = fixture.name,
                    nationality = fixture.nationality,
                    languages = language,
                    interest = fixture.interest,
                )

                assertSoftly {
                    // Common
                    result.platform.provider shouldBe fixture.platform.provider
                    result.platform.socialId shouldBe fixture.platform.socialId
                    result.platform.email!!.value shouldBe fixture.platform.email!!.value
                    result.name shouldBe fixture.name
                    result.nationality shouldBe fixture.nationality
                    result.introduction shouldBe null
                    result.profileImageUrl shouldBe null
                    result.profileComplete shouldBe false
                    result.status shouldBe Member.Status.ACTIVE
                    result.role shouldBe Role.MENTEE
                    result.languages.map { it.category } shouldContainExactly language.map { it.category }
                    result.languages.map { it.type } shouldContainExactly language.map { it.type }

                    // Mentee
                    result.interest.school shouldBe fixture.interest.school
                    result.interest.major shouldBe fixture.interest.major
                }
            }
        }
    }

    feature("Mentee's completeProfile & profileComplete") {
        val fixtureA = menteeFixture(id = 1L)
        val fixtureB = menteeFixture(id = 2L)
        val fixtureC = menteeFixture(id = 3L)

        scenario("Mentee 프로필이 완성되었는지 확인한다 [자기소개 & 프로필 이미지]") {
            /**
             * MenteeA
             */
            // 완성
            val menteeA: Mentee = fixtureA.toDomain()
            menteeA.profileComplete shouldBe true

            /**
             * MenteeB
             */
            // 미완성
            val menteeB = Mentee(
                id = fixtureB.id,
                platform = fixtureB.platform,
                name = fixtureB.name,
                nationality = fixtureB.nationality,
                languages = fixtureB.languages,
                interest = fixtureB.interest,
            )
            menteeB.profileComplete shouldBe false

            // 완성
            menteeB.completeProfile(
                introduction = fixtureB.introduction,
                profileImageUrl = fixtureB.profileImageUrl,
            )
            menteeB.profileComplete shouldBe true

            /**
             * MenteeC
             */
            // 완성
            val menteeC: Mentee = fixtureC.toDomain()
            menteeC.profileComplete shouldBe true

            // 미완성
            menteeC.completeProfile(
                introduction = null,
                profileImageUrl = fixtureC.profileImageUrl,
            )
            menteeC.profileComplete shouldBe false
        }
    }

    feature("Mentee's updateBasicInfo") {
        val fixtureA = menteeFixture(sequence = 1)
        val fixtureB = menteeFixture(sequence = 2)

        scenario("Mentee 기본 정보를 수정한다") {
            val mentee: Mentee = fixtureA.toDomain()

            mentee.updateBasicInfo(
                name = fixtureB.name,
                nationality = fixtureB.nationality,
                profileImageUrl = fixtureB.profileImageUrl,
                introduction = fixtureB.introduction,
                languages = fixtureB.languages,
                interestSchool = fixtureB.interest.school,
                interestMajor = fixtureB.interest.major,
            )
            assertSoftly {
                // Effected
                mentee.name shouldBe fixtureB.name
                mentee.nationality shouldBe fixtureB.nationality
                mentee.introduction shouldBe fixtureB.introduction
                mentee.profileImageUrl shouldBe fixtureB.profileImageUrl
                mentee.languages.map { it.category } shouldContainExactly fixtureB.languages.map { it.category }
                mentee.languages.map { it.type } shouldContainExactly fixtureB.languages.map { it.type }
                mentee.interest.school shouldBe fixtureB.interest.school
                mentee.interest.major shouldBe fixtureB.interest.major

                // Not Effected
                mentee.platform.provider shouldBe fixtureA.platform.provider
                mentee.platform.socialId shouldBe fixtureA.platform.socialId
                mentee.platform.email!!.value shouldBe fixtureA.platform.email!!.value
                mentee.profileComplete shouldBe true
                mentee.status shouldBe Member.Status.ACTIVE
                mentee.role shouldBe Role.MENTEE
            }
        }
    }
})
