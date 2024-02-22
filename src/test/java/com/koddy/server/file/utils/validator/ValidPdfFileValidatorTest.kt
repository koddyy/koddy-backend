package com.koddy.server.file.utils.validator

import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import jakarta.validation.ConstraintValidatorContext

@DisplayName("File -> ValidPdfFileValidator 테스트")
internal class ValidPdfFileValidatorTest : DescribeSpec({
    val context: ConstraintValidatorContext = mockk<ConstraintValidatorContext>()
    val sut = ValidPdfFileValidator()

    describe("ValidImageFileValidator's isValid") {
        context("PDF 파일 확장자가 아니면") {
            val fileNames: List<String> = listOf(
                "hello.jpg",
                "hello.jpeg",
                "hello.png",
            )

            it("Validator를 통과하지 못한다") {
                fileNames.forEach {
                    sut.isValid(it, context) shouldBe false
                }
            }
        }

        context("PDF 파일 확장자면") {
            val fileName = "hello.pdf"

            it("Validator를 통과한다") {
                sut.isValid(fileName, context) shouldBe true
            }
        }
    }
})
