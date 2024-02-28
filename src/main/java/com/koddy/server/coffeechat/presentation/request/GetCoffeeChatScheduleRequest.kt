package com.koddy.server.coffeechat.presentation.request

import com.koddy.server.coffeechat.application.usecase.query.GetMenteeCoffeeChats
import com.koddy.server.coffeechat.application.usecase.query.GetMentorCoffeeChats
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class GetCoffeeChatScheduleRequest(
    @field:NotBlank(message = "필터링 카테고리는 필수입니다.")
    val category: String,

    val detail: String?,

    @field:NotNull(message = "페이지 번호는 필수입니다.")
    @field:Min(value = 1, message = "페이지는 1부터 시작입니다.")
    val page: Int,
) {
    fun toMentorQuery(mentorId: Long): GetMentorCoffeeChats =
        GetMentorCoffeeChats(
            mentorId = mentorId,
            status = convertToCoffeeChatStatus(),
            page = page,
        )

    fun toMenteeQuery(menteeId: Long): GetMenteeCoffeeChats =
        GetMenteeCoffeeChats(
            menteeId = menteeId,
            status = convertToCoffeeChatStatus(),
            page = page,
        )

    private fun convertToCoffeeChatStatus(): List<CoffeeChatStatus> {
        if (detail.isNullOrBlank()) {
            return CoffeeChatStatus.fromCategory(CoffeeChatStatus.Category.from(category))
        }
        return CoffeeChatStatus.fromCategoryDetail(
            CoffeeChatStatus.Category.from(category),
            CoffeeChatStatus.Detail.from(detail),
        )
    }
}
