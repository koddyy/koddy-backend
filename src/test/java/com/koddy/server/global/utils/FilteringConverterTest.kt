package com.koddy.server.global.utils

import com.koddy.server.common.UnitTestKt
import com.koddy.server.global.utils.FilteringConverter.convertToLanguage
import com.koddy.server.global.utils.FilteringConverter.convertToNationality
import com.koddy.server.member.domain.model.Language
import com.koddy.server.member.domain.model.Nationality
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactly
import org.junit.jupiter.api.DisplayName

@UnitTestKt
@DisplayName("Global/Utils -> FilteringConverter 테스트")
internal class FilteringConverterTest : DescribeSpec({
    describe("FilteringConverter's convertToNationality") {
        context("N개의 국적 코드가 콤마(,)로 구분지어 있으면") {
            val value = "KR,EN,CN,JP,VN,ETC"

            it("List<Nationality> 도메인으로 변환한다") {
                val result: List<Nationality> = convertToNationality(value)

                result shouldContainExactly listOf(
                    Nationality.KOREA,
                    Nationality.USA,
                    Nationality.JAPAN,
                    Nationality.CHINA,
                    Nationality.VIETNAM,
                    Nationality.ETC
                )
            }
        }
    }

    describe("FilteringConverter's convertToLanguage") {
        context("N개의 언어 코드가 콤마(,)로 구분지어 있으면") {
            val value = "KR,EN,CN,JP,VN"

            it("List<Language.Category> 도메인으로 변환한다") {
                val result: List<Language.Category> = convertToLanguage(value)

                result shouldContainExactly listOf(
                    Language.Category.KR,
                    Language.Category.EN,
                    Language.Category.CN,
                    Language.Category.JP,
                    Language.Category.VN
                )
            }
        }
    }
})
