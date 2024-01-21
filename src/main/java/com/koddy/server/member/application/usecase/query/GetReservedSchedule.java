package com.koddy.server.member.application.usecase.query;

public record GetReservedSchedule(
        long mentorId,
        int year,
        int month
) {
}
