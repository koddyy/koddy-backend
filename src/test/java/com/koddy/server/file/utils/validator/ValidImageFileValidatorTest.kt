package com.koddy.server.file.utils.validator

import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import jakarta.validation.ConstraintValidatorContext

@DisplayName("File -> ValidImageFileValidator 테스트")
internal class ValidImageFileValidatorTest : DescribeSpec({
    val context: ConstraintValidatorContext = mockk<ConstraintValidatorContext>()
    val sut = ValidImageFileValidator()

    describe("ValidImageFileValidator's isValid") {
        context("이미지 파일 확장자가 아니면 [JPG, JPEG, PNG]") {
            val fileNames: List<String> = listOf(
                "hello.pdf",
                "hello.xls",
            )

            it("Validator를 통과하지 못한다") {
                fileNames.forEach {
                    sut.isValid(it, context) shouldBe false
                }
            }
        }

        context("이미지 파일 확장자면 [JPG, JPEG, PNG]") {
            val fileNames: List<String> = listOf(
                "hello.jpg",
                "hello.jpeg",
                "hello.png",
            )

            it("Validator를 통과한다") {
                fileNames.forEach {
                    sut.isValid(it, context) shouldBe true
                }
            }
        }
    }
})
