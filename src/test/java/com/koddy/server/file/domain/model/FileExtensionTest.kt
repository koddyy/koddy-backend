package com.koddy.server.file.domain.model

import com.koddy.server.file.exception.FileException
import com.koddy.server.file.exception.FileExceptionCode
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage

@DisplayName("File -> 도메인 [FileExtension] 테스트")
internal class FileExtensionTest : DescribeSpec({
    describe("FileExtension's from") {
        context("제공하지 않는 파일 확장자면") {
            val files: List<String> = listOf(
                "hello.gif",
                "hello.mp3",
                "hello.xls",
                "hello.alz",
            )

            it("INVALID_FILE_EXTENSION 예외가 발생한다") {
                files.forEach {
                    shouldThrow<FileException> {
                        FileExtension.from(it)
                    } shouldHaveMessage FileExceptionCode.INVALID_FILE_EXTENSION.message
                }
            }
        }

        context("제공하는 파일 확장자면") {
            val files: Map<String, FileExtension> = mapOf(
                "hello.jpg" to FileExtension.JPG,
                "hello.jpeg" to FileExtension.JPEG,
                "hello.png" to FileExtension.PNG,
                "hello.pdf" to FileExtension.PDF,
            )

            it("확장자를 추출해서 FileExtension 도메인을 반환한다") {
                files.forEach { (fileName: String, result: FileExtension) -> FileExtension.from(fileName) shouldBe result }
            }
        }
    }

    describe("FileExtension's isImage") {
        context("주어진 파일명에 대해서") {
            val files: Map<String, Boolean> = mapOf(
                "hello.jpg" to true,
                "hello.jpeg" to true,
                "hello.png" to true,
                "hello.pdf" to false,
            )

            it("이미지 파일인지 확인한다") {
                files.forEach { (fileName: String, result: Boolean) -> FileExtension.isImage(fileName) shouldBe result }
            }
        }
    }

    describe("FileExtension's isPdf") {
        context("주어진 파일명에 대해서") {
            val files: Map<String, Boolean> = mapOf(
                "hello.jpg" to false,
                "hello.jpeg" to false,
                "hello.png" to false,
                "hello.pdf" to true,
            )

            it("PDF 파일인지 확인한다") {
                files.forEach { (fileName: String, result: Boolean) -> FileExtension.isPdf(fileName) shouldBe result }
            }
        }
    }
})
