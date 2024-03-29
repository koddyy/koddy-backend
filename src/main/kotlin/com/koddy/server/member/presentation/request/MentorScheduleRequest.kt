package com.koddy.server.member.presentation.request

import com.koddy.server.global.utils.TimeUtils
import com.koddy.server.member.domain.model.mentor.DayOfWeek
import com.koddy.server.member.domain.model.mentor.Timeline

data class MentorScheduleRequest(
    val dayOfWeek: String,
    val start: Start,
    val end: End,
) {
    data class Start(
        val hour: Int,
        val minute: Int,
    )

    data class End(
        val hour: Int,
        val minute: Int,
    )

    fun toTimeline(): Timeline {
        return Timeline(
            dayOfWeek = DayOfWeek.from(dayOfWeek),
            startTime = TimeUtils.toLocalTime(start.hour, start.minute),
            endTime = TimeUtils.toLocalTime(end.hour, end.minute),
        )
    }
}
