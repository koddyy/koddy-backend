package com.koddy.server.member.application.usecase.query;

public record GetMentorReservedSchedule(
        long mentorId,
        int year,
        int month
) {
}
