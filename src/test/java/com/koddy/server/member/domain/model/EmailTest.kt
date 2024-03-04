package com.koddy.server.member.domain.model

import com.koddy.server.member.exception.MemberException
import com.koddy.server.member.exception.MemberExceptionCode.INVALID_EMAIL_PATTERN
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.throwable.shouldHaveMessage

@DisplayName("Member -> 도메인 [Email] 테스트")
internal class EmailTest : DescribeSpec({
    describe("Email 생성") {
        context("형식에 맞지 않는 Email이면") {
            it("INVALID_EMAIL_PATTERN 예외가 발생한다") {
                listOf(
                    "",
                    "abc",
                    "@gmail.com",
                    "abc@gmail",
                    "abc@gmail.c",
                ).forEach {
                    shouldThrow<MemberException> {
                        Email(it)
                    } shouldHaveMessage INVALID_EMAIL_PATTERN.message
                }
            }
        }

        context("형식에 맞는 Email이면") {
            it("Email을 생성할 수 있다") {
                listOf(
                    "sjiwon@gmail.com",
                    "sjiwon@kakao.com",
                    "sjiwon@naver.com",
                    "sjiwon@nate.com",
                ).forEach { shouldNotThrowAny { Email(it) } }
            }
        }
    }
})
