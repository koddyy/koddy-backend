package com.koddy.server.file.utils.converter

import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.utils.FileVirtualCreator.createFile
import com.koddy.server.file.domain.model.FileExtension
import com.koddy.server.file.domain.model.RawFileData
import com.koddy.server.file.exception.FileException
import com.koddy.server.file.exception.FileExceptionCode
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile

@UnitTestKt
@DisplayName("File -> FileConverter 테스트")
internal class FileConverterTest : DescribeSpec({
    describe("FileConverter's convertFile") {
        context("MultipartFile's content가 비어있으면") {
            val file: MultipartFile = MockMultipartFile("cat.png", ByteArray(0))

            it("FILE_NOT_UPLOADED 예외가 발생한다") {
                shouldThrow<FileException> {
                    FileConverter.convertFile(file)
                } shouldHaveMessage FileExceptionCode.FILE_NOT_UPLOADED.message
            }
        }

        context("MultipartFile's content가 존재하면") {
            val file: MultipartFile = createFile("cat.png", "image/png")

            it("RawFileData 도메인으로 변환한다") {
                val result: RawFileData = FileConverter.convertFile(file)

                assertSoftly(result) {
                    fileName shouldBe "cat.png"
                    contentType shouldBe "image/png"
                    extension shouldBe FileExtension.PNG
                }
            }
        }
    }
})
