package com.koddy.server.global.utils

import com.koddy.server.auth.domain.model.Authenticated
import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.fixture.MentorFixture
import com.koddy.server.global.exception.GlobalException
import com.koddy.server.global.exception.GlobalExceptionCode
import com.koddy.server.member.domain.model.Member
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.throwable.shouldHaveMessage

@UnitTestKt
@DisplayName("Global/Utils -> UniversityInfo 테스트")
internal class UniversityInfoTest : DescribeSpec({
    describe("UniversityInfo's validateDomain") {
        val member: Member<*> = MentorFixture.MENTOR_1.toDomain().apply(1L)
        val authenticated = Authenticated(member.id, member.authority)

        context("시스템에서 관리하지 않는 대학교 도메인이면") {
            val domains = listOf(
                "sjiwon@kyonggi.edu",
                "sjiwon@kgu.edu",
                "sjiwon@kaya.edu",
                "gachon@snu.edu",
            )

            it("NOT_PROVIDED_UNIV_DOMAIN 예외가 발생한다") {
                domains.forEach {
                    shouldThrow<GlobalException> {
                        UniversityInfo.validateDomain(authenticated, it)
                    } shouldHaveMessage GlobalExceptionCode.NOT_PROVIDED_UNIV_DOMAIN.message
                }
            }
        }

        context("시스템에서 관리하고 있는 대학교 도메인이면") {
            val domains = listOf(
                "sjiwon@kyonggi.ac.kr",
                "sjiwon@kgu.ac.kr",
                "sjiwon@kaya.ac.kr",
                "gachon@snu.ac.kr",
            )

            it("검증에 성공한다") {
                domains.forEach {
                    shouldNotThrowAny {
                        UniversityInfo.validateDomain(authenticated, it)
                    }
                }
            }
        }
    }
})
