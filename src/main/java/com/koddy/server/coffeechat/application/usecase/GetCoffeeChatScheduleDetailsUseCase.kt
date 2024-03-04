package com.koddy.server.coffeechat.application.usecase

import com.koddy.server.coffeechat.application.usecase.query.GetCoffeeChatScheduleDetails
import com.koddy.server.coffeechat.application.usecase.query.response.MenteeCoffeeChatScheduleDetails
import com.koddy.server.coffeechat.application.usecase.query.response.MentorCoffeeChatScheduleDetails
import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.model.response.CoffeeChatScheduleDetails
import com.koddy.server.coffeechat.domain.service.CoffeeChatReader
import com.koddy.server.global.annotation.KoddyReadOnlyTransactional
import com.koddy.server.global.annotation.UseCase
import com.koddy.server.global.utils.encrypt.Encryptor
import com.koddy.server.member.domain.model.Language
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.service.MemberReader

@UseCase
class GetCoffeeChatScheduleDetailsUseCase(
    private val coffeeChatReader: CoffeeChatReader,
    private val memberReader: MemberReader,
    private val encryptor: Encryptor,
) {
    @KoddyReadOnlyTransactional
    fun invoke(query: GetCoffeeChatScheduleDetails): CoffeeChatScheduleDetails {
        val coffeeChat: CoffeeChat = coffeeChatReader.getById(query.coffeeChatId)

        return when (query.authenticated.isMentor) {
            true -> {
                val mentee: Mentee = memberReader.getMenteeWithNative(coffeeChat.menteeId)
                val languages: List<Language> = fetchMemberLanguages(mentee.id)
                MentorCoffeeChatScheduleDetails.of(mentee, languages, coffeeChat, encryptor)
            }

            false -> {
                val mentor: Mentor = memberReader.getMentorWithNative(coffeeChat.mentorId)
                val languages: List<Language> = fetchMemberLanguages(mentor.id)
                MenteeCoffeeChatScheduleDetails.of(mentor, languages, coffeeChat, encryptor)
            }
        }
    }

    private fun fetchMemberLanguages(memberId: Long): List<Language> {
        return memberReader.getMemberAvailableLanguagesWithNative(memberId).map { it.language }
    }
}
