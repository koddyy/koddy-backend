package com.koddy.server.member.domain.model.response

import com.koddy.server.member.domain.model.mentor.Timeline

data class ScheduleResponse(
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

    companion object {
        fun from(timeline: Timeline): ScheduleResponse =
            ScheduleResponse(
                dayOfWeek = timeline.dayOfWeek.kor,
                start = Start(
                    hour = timeline.startTime.hour,
                    minute = timeline.startTime.minute,
                ),
                end = End(
                    hour = timeline.endTime.hour,
                    minute = timeline.endTime.minute,
                ),
            )
    }
}
