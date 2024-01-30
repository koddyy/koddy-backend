package com.koddy.server.member.application.usecase.query.response;

public record CarouselProfileResponse<T>(
        T result,
        long totalCount
) {
}
