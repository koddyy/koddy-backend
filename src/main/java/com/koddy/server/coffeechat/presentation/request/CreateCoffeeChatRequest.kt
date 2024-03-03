package com.koddy.server.coffeechat.presentation.request

import com.fasterxml.jackson.annotation.JsonFormat
import com.koddy.server.coffeechat.application.usecase.command.CreateCoffeeChatByApplyCommand
import com.koddy.server.coffeechat.application.usecase.command.CreateCoffeeChatBySuggestCommand
import com.koddy.server.coffeechat.domain.model.Reservation
import com.koddy.server.global.utils.TimeUtils
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CreateCoffeeChatByApplyRequest(
    @field:NotNull(message = "멘토 정보는 필수입니다.")
    val mentorId: Long,

    @field:NotBlank(message = "멘토에게 커피챗을 신청하는 이유를 입력해주세요.")
    val applyReason: String,

    @field:NotBlank(message = "커피챗 시작 날짜를 선택해주세요.")
    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val start: String,

    @field:NotBlank(message = "커피챗 종료 날짜를 선택해주세요.")
    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val end: String,
) {
    fun toCommand(menteeId: Long): CreateCoffeeChatByApplyCommand {
        return CreateCoffeeChatByApplyCommand(
            menteeId = menteeId,
            mentorId = mentorId,
            applyReason = applyReason,
            reservation = Reservation.of(
                TimeUtils.toLocalDateTime(start),
                TimeUtils.toLocalDateTime(end),
            ),
        )
    }
}

data class CreateCoffeeChatBySuggestRequest(
    @field:NotNull(message = "멘티 정보는 필수입니다.")
    val menteeId: Long,

    @field:NotBlank(message = "멘티에게 커피챗을 제안하는 이유를 입력해주세요.")
    val suggestReason: String,
) {
    fun toCommand(mentorId: Long): CreateCoffeeChatBySuggestCommand {
        return CreateCoffeeChatBySuggestCommand(
            mentorId = mentorId,
            menteeId = menteeId,
            suggestReason = suggestReason,
        )
    }
}
