package com.koddy.server.member.application.usecase.query;

public record GetReservedMentoringTime(
        long mentorId,
        int year,
        int month
) {
}
