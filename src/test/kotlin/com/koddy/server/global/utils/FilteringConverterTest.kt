package com.koddy.server.global.utils

import com.koddy.server.common.UnitTestKt
import com.koddy.server.member.domain.model.Language
import com.koddy.server.member.domain.model.Nationality
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactly

@UnitTestKt
@DisplayName("Global/Utils -> FilteringConverter 테스트")
internal class FilteringConverterTest : DescribeSpec({
    describe("FilteringConverter's convertToNationality") {
        context("N개의 국적 코드가 콤마(,)로 구분되어 있으면") {
            val value = "KR,EN,CN,JP,VN,ETC"

            it("List<Nationality> 도메인으로 변환한다") {
                val result: List<Nationality> = FilteringConverter.convertToNationality(value)

                result shouldContainExactly listOf(
                    Nationality.KOREA,
                    Nationality.USA,
                    Nationality.JAPAN,
                    Nationality.CHINA,
                    Nationality.VIETNAM,
                    Nationality.ETC,
                )
            }
        }
    }

    describe("FilteringConverter's convertToLanguage") {
        context("N개의 언어 코드가 콤마(,)로 구분되어 있으면") {
            val value = "KR,EN,CN,JP,VN"

            it("List<Language.Category> 도메인으로 변환한다") {
                val result: List<Language.Category> = FilteringConverter.convertToLanguage(value)

                result shouldContainExactly listOf(
                    Language.Category.KR,
                    Language.Category.EN,
                    Language.Category.CN,
                    Language.Category.JP,
                    Language.Category.VN,
                )
            }
        }
    }
})
