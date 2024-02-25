package com.koddy.server.coffeechat.domain.event

sealed class MentorNotification(
    open val mentorId: Long,
    open val coffeeChatId: Long,
) {
    data class MenteeAppliedFromMenteeFlowEvent(
        override val mentorId: Long,
        override val coffeeChatId: Long,
    ) : MentorNotification(mentorId, coffeeChatId)

    data class MenteeCanceledFromMenteeFlowEvent(
        override val mentorId: Long,
        override val coffeeChatId: Long,
    ) : MentorNotification(mentorId, coffeeChatId)

    data class MenteeCanceledFromMentorFlowEvent(
        override val mentorId: Long,
        override val coffeeChatId: Long,
    ) : MentorNotification(mentorId, coffeeChatId)

    data class MenteeRejectedFromMentorFlowEvent(
        override val mentorId: Long,
        override val coffeeChatId: Long,
    ) : MentorNotification(mentorId, coffeeChatId)

    data class MenteePendedFromMentorFlowEvent(
        override val mentorId: Long,
        override val coffeeChatId: Long,
    ) : MentorNotification(mentorId, coffeeChatId)
}

sealed class MenteeNotification(
    open val menteeId: Long,
    open val coffeeChatId: Long,
) {
    data class MentorCanceledFromMenteeFlowEvent(
        override val menteeId: Long,
        override val coffeeChatId: Long,
    ) : MenteeNotification(menteeId, coffeeChatId)

    data class MentorRejectedFromMenteeFlowEvent(
        override val menteeId: Long,
        override val coffeeChatId: Long,
    ) : MenteeNotification(menteeId, coffeeChatId)

    data class MentorSuggestedFromMentorFlowEvent(
        override val menteeId: Long,
        override val coffeeChatId: Long,
    ) : MenteeNotification(menteeId, coffeeChatId)

    data class MentorCanceledFromMentorFlowEvent(
        override val menteeId: Long,
        override val coffeeChatId: Long,
    ) : MenteeNotification(menteeId, coffeeChatId)

    data class MentorFinallyCanceledFromMentorFlowEvent(
        override val menteeId: Long,
        override val coffeeChatId: Long,
    ) : MenteeNotification(menteeId, coffeeChatId)
}

sealed class BothNotification(
    open val menteeId: Long,
    open val mentorId: Long,
    open val coffeeChatId: Long,
) {
    data class ApprovedFromMenteeFlowEvent(
        override val menteeId: Long,
        override val mentorId: Long,
        override val coffeeChatId: Long,
    ) : BothNotification(menteeId, mentorId, coffeeChatId)

    data class FinallyApprovedFromMentorFlowEvent(
        override val menteeId: Long,
        override val mentorId: Long,
        override val coffeeChatId: Long,
    ) : BothNotification(menteeId, mentorId, coffeeChatId)
}
