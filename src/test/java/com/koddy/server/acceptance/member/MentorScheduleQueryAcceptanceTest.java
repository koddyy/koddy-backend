package com.koddy.server.acceptance.member;

import com.koddy.server.auth.domain.model.AuthMember;
import com.koddy.server.common.AcceptanceTest;
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension;
import com.koddy.server.common.fixture.MentorFixture;
import com.koddy.server.common.fixture.StrategyFixture;
import com.koddy.server.member.domain.model.mentor.MentoringPeriod;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_Pending_상태인_커피챗에_대해서_최종_거절을_한다;
import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_Pending_상태인_커피챗에_대해서_최종_수락을_한다;
import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다;
import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_멘티의_커피챗_신청을_수락한다;
import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다;
import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘티가_멘토의_커피챗_제안을_1차_수락한다;
import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘티가_멘토의_커피챗_제안을_거절한다;
import static com.koddy.server.acceptance.member.MemberAcceptanceStep.멘토의_예약된_스케줄_정보를_조회한다;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_10;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_2;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_3;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_4;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_5;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_6;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_7;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_8;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_9;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(DatabaseCleanerEachCallbackExtension.class)
@DisplayName("[Acceptance Test] 멘토 예약된 스케줄 조회")
public class MentorScheduleQueryAcceptanceTest extends AcceptanceTest {
    @Nested
    @DisplayName("특정 Year-Month에 대해서 멘토의 예약된 스케줄 정보를 조회한다")
    class GetReservedSchedule {
        @Test
        @DisplayName("멘토링 시간 정보를 기입하지 않은 멘토에 대한 조회")
        void successA() {
            final AuthMember mentor = MENTOR_1.회원가입과_로그인을_진행한다();
            final AuthMember mentee = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다();
            멘토의_예약된_스케줄_정보를_조회한다(mentor.id(), 2024, 2, mentee.token().accessToken())
                    .statusCode(OK.value())
                    .body("period", nullValue())
                    .body("schedules", hasSize(0))
                    .body("timeUnit", nullValue())
                    .body("reserved", hasSize(0));
        }

        @Test
        @DisplayName("멘토링 시간 정보를 기입한 멘토에 대한 조회 + 예약 정보 X")
        void successB() {
            final AuthMember mentor = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다();
            final AuthMember mentee = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다();
            멘토의_예약된_스케줄_정보를_조회한다(mentor.id(), 2024, 2, mentee.token().accessToken())
                    .statusCode(OK.value())
                    .body("period.startDate", is(MENTOR_1.getMentoringPeriod().getStartDate().toString()))
                    .body("period.endDate", is(MENTOR_1.getMentoringPeriod().getEndDate().toString()))
                    .body("schedules", hasSize(MENTOR_1.getTimelines().size()))
                    .body("timeUnit", is(MentoringPeriod.TimeUnit.HALF_HOUR.getValue()))
                    .body("reserved", hasSize(0));
        }

        @Test
        @DisplayName("멘토링 시간 정보를 기입한 멘토에 대한 조회 + 예약 정보 O")
        void successC() {
            final AuthMember mentor = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다();
            final AuthMember mentee1 = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다();
            final AuthMember mentee2 = MENTEE_2.회원가입과_로그인을_하고_프로필을_완성시킨다();
            final AuthMember mentee3 = MENTEE_3.회원가입과_로그인을_하고_프로필을_완성시킨다();
            final AuthMember mentee4 = MENTEE_4.회원가입과_로그인을_하고_프로필을_완성시킨다();
            final AuthMember mentee5 = MENTEE_5.회원가입과_로그인을_하고_프로필을_완성시킨다();
            final AuthMember mentee6 = MENTEE_6.회원가입과_로그인을_하고_프로필을_완성시킨다();
            final AuthMember mentee7 = MENTEE_7.회원가입과_로그인을_하고_프로필을_완성시킨다();
            final AuthMember mentee8 = MENTEE_8.회원가입과_로그인을_하고_프로필을_완성시킨다();
            final AuthMember mentee9 = MENTEE_9.회원가입과_로그인을_하고_프로필을_완성시킨다();
            final AuthMember mentee10 = MENTEE_10.회원가입과_로그인을_하고_프로필을_완성시킨다();

            final long coffeeChat1 = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee1.id(), mentor.token().accessToken());
            final long coffeeChat2 = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee2.id(), mentor.token().accessToken());
            final long coffeeChat3 = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee3.id(), mentor.token().accessToken());
            final long coffeeChat4 = 멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(
                    LocalDateTime.of(2024, 2, 19, 18, 0),
                    LocalDateTime.of(2024, 2, 19, 18, 30),
                    mentor.id(),
                    mentee4.token().accessToken()
            );
            final long coffeeChat5 = 멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(
                    LocalDateTime.of(2024, 3, 4, 18, 0),
                    LocalDateTime.of(2024, 3, 4, 18, 30),
                    mentor.id(),
                    mentee5.token().accessToken()
            );
            final long coffeeChat6 = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee6.id(), mentor.token().accessToken());
            final long coffeeChat7 = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee7.id(), mentor.token().accessToken());
            final long coffeeChat8 = 멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(
                    LocalDateTime.of(2024, 4, 5, 18, 0),
                    LocalDateTime.of(2024, 4, 5, 18, 30),
                    mentor.id(),
                    mentee8.token().accessToken()
            );
            final long coffeeChat9 = 멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(
                    LocalDateTime.of(2024, 4, 17, 18, 0),
                    LocalDateTime.of(2024, 4, 17, 18, 30),
                    mentor.id(),
                    mentee9.token().accessToken()
            );
            final long coffeeChat10 = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee10.id(), mentor.token().accessToken());

            멘티가_멘토의_커피챗_제안을_1차_수락한다(
                    coffeeChat1,
                    LocalDateTime.of(2024, 2, 5, 18, 0),
                    LocalDateTime.of(2024, 2, 5, 18, 30),
                    mentee1.token().accessToken()
            );
            멘티가_멘토의_커피챗_제안을_거절한다(coffeeChat3, "거절..", mentee3.token().accessToken());
            멘토가_멘티의_커피챗_신청을_수락한다(coffeeChat5, StrategyFixture.KAKAO_ID, mentor.token().accessToken());
            멘토가_멘티의_커피챗_신청을_수락한다(coffeeChat9, StrategyFixture.KAKAO_ID, mentor.token().accessToken());
            멘티가_멘토의_커피챗_제안을_1차_수락한다(
                    coffeeChat6,
                    LocalDateTime.of(2024, 3, 15, 18, 0),
                    LocalDateTime.of(2024, 3, 15, 18, 30),
                    mentee6.token().accessToken()
            );
            멘토가_Pending_상태인_커피챗에_대해서_최종_수락을_한다(coffeeChat6, StrategyFixture.KAKAO_ID, mentor.token().accessToken());
            멘티가_멘토의_커피챗_제안을_1차_수락한다(
                    coffeeChat7,
                    LocalDateTime.of(2024, 4, 1, 18, 0),
                    LocalDateTime.of(2024, 4, 1, 18, 30),
                    mentee7.token().accessToken()
            );
            멘토가_Pending_상태인_커피챗에_대해서_최종_거절을_한다(coffeeChat7, "거절..", mentor.token().accessToken());
            멘티가_멘토의_커피챗_제안을_1차_수락한다(
                    coffeeChat10,
                    LocalDateTime.of(2024, 4, 10, 18, 0),
                    LocalDateTime.of(2024, 4, 10, 18, 30),
                    mentee10.token().accessToken()
            );

            final ValidatableResponse response1 = 멘토의_예약된_스케줄_정보를_조회한다(mentor.id(), 2024, 1, mentee1.token().accessToken())
                    .statusCode(OK.value());
            assertReservedScheduleMatch(
                    response1,
                    MENTOR_1,
                    List.of(),
                    List.of()
            );

            final ValidatableResponse response2 = 멘토의_예약된_스케줄_정보를_조회한다(mentor.id(), 2024, 2, mentee1.token().accessToken())
                    .statusCode(OK.value());
            assertReservedScheduleMatch(
                    response2,
                    MENTOR_1,
                    List.of(
                            LocalDateTime.of(2024, 2, 5, 18, 0, 0),
                            LocalDateTime.of(2024, 2, 19, 18, 0, 0)
                    ),
                    List.of(
                            LocalDateTime.of(2024, 2, 5, 18, 30, 0),
                            LocalDateTime.of(2024, 2, 19, 18, 30, 0)
                    )
            );

            final ValidatableResponse response3 = 멘토의_예약된_스케줄_정보를_조회한다(mentor.id(), 2024, 3, mentee1.token().accessToken())
                    .statusCode(OK.value());
            assertReservedScheduleMatch(
                    response3,
                    MENTOR_1,
                    List.of(
                            LocalDateTime.of(2024, 3, 4, 18, 0, 0),
                            LocalDateTime.of(2024, 3, 15, 18, 0, 0)
                    ),
                    List.of(
                            LocalDateTime.of(2024, 3, 4, 18, 30, 0),
                            LocalDateTime.of(2024, 3, 15, 18, 30, 0)
                    )
            );

            final ValidatableResponse response4 = 멘토의_예약된_스케줄_정보를_조회한다(mentor.id(), 2024, 4, mentee1.token().accessToken())
                    .statusCode(OK.value());
            assertReservedScheduleMatch(
                    response4,
                    MENTOR_1,
                    List.of(
                            LocalDateTime.of(2024, 4, 5, 18, 0, 0),
                            LocalDateTime.of(2024, 4, 10, 18, 0, 0),
                            LocalDateTime.of(2024, 4, 17, 18, 0, 0)
                    ),
                    List.of(
                            LocalDateTime.of(2024, 4, 5, 18, 30, 0),
                            LocalDateTime.of(2024, 4, 10, 18, 30, 0),
                            LocalDateTime.of(2024, 4, 17, 18, 30, 0)
                    )
            );

            final ValidatableResponse response5 = 멘토의_예약된_스케줄_정보를_조회한다(mentor.id(), 2024, 5, mentee1.token().accessToken())
                    .statusCode(OK.value());
            assertReservedScheduleMatch(
                    response5,
                    MENTOR_1,
                    List.of(),
                    List.of()
            );
        }
    }

    private void assertReservedScheduleMatch(
            final ValidatableResponse response,
            final MentorFixture mentor,
            final List<LocalDateTime> reservedStart,
            final List<LocalDateTime> reservedEnd
    ) {
        response
                .body("period.startDate", is(mentor.getMentoringPeriod().getStartDate().toString()))
                .body("period.endDate", is(mentor.getMentoringPeriod().getEndDate().toString()))
                .body("schedules.dayOfWeek", contains(
                        mentor.getTimelines()
                                .stream()
                                .map(it -> it.getDayOfWeek().getKor())
                                .toList()
                                .toArray()
                ))
                .body("schedules.start.hour", contains(
                        mentor.getTimelines()
                                .stream()
                                .map(it -> it.getStartTime().getHour())
                                .toList()
                                .toArray()
                ))
                .body("schedules.start.minute", contains(
                        mentor.getTimelines()
                                .stream()
                                .map(it -> it.getStartTime().getMinute())
                                .toList()
                                .toArray()
                ))
                .body("schedules.end.hour", contains(
                        mentor.getTimelines()
                                .stream()
                                .map(it -> it.getEndTime().getHour())
                                .toList()
                                .toArray()
                ))
                .body("schedules.end.minute", contains(
                        mentor.getTimelines()
                                .stream()
                                .map(it -> it.getEndTime().getMinute())
                                .toList()
                                .toArray()
                ))
                .body("timeUnit", is(MentoringPeriod.TimeUnit.HALF_HOUR.getValue()));

        if (reservedStart.isEmpty()) {
            response.body("reserved", hasSize(0));
        } else {
            response
                    .body("reserved.start", contains(
                            reservedStart.stream()
                                    .map(it -> it.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                                    .toList()
                                    .toArray()
                    ))
                    .body("reserved.end", contains(
                            reservedEnd.stream()
                                    .map(it -> it.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                                    .toList()
                                    .toArray()
                    ));
        }
    }
}
