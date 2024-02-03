package com.koddy.server.acceptance.coffeechat;

import com.koddy.server.auth.domain.model.AuthMember;
import com.koddy.server.common.AcceptanceTest;
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension;
import com.koddy.server.common.fixture.MenteeFixture;
import com.koddy.server.common.fixture.MentorFixture;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.내_일정_커피챗_상세_조회를_진행한다;
import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_Pending_상태인_커피챗에_대해서_최종_거절을_한다;
import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_Pending_상태인_커피챗에_대해서_최종_수락을_한다;
import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다;
import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_멘티의_커피챗_신청을_거절한다;
import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_멘티의_커피챗_신청을_수락한다;
import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다;
import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘티가_멘토의_커피챗_제안을_1차_수락한다;
import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘티가_멘토의_커피챗_제안을_거절한다;
import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.신청_제안한_커피챗을_취소한다;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.APPLY;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.APPROVE;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.CANCEL;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.PENDING;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.REJECT;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.SUGGEST;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_20_00_시작;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.fixture.StrategyFixture.KAKAO_ID;
import static com.koddy.server.member.domain.model.Language.Category.EN;
import static com.koddy.server.member.domain.model.Language.Category.KR;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(DatabaseCleanerEachCallbackExtension.class)
@DisplayName("[Acceptance Test] 내 일정 커피챗 상세 조회")
public class CoffeeChatScheduleDetailsQueryAcceptanceTest extends AcceptanceTest {
    private static final LocalDateTime start = 월요일_1주차_20_00_시작.getStart();
    private static final LocalDateTime end = 월요일_1주차_20_00_시작.getEnd();

    private AuthMember mentor;
    private AuthMember mentee;

    @BeforeEach
    void setUp() {
        mentor = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다();
        mentee = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다();
    }

    @Nested
    @DisplayName("내 일정 커피챗 상세 조회를 진행한다 [MenteeFlow]")
    class GetCoffeeChatScheduleDetailsWithMenteeFlow {
        @Test
        @DisplayName("1. APPLY 상태 커피챗 상세 조회")
        void apply() {
            final long coffeeChatId = 멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(start, end, mentor.id(), mentee.token().accessToken());

            final ValidatableResponse mentorResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentor.token().accessToken()).statusCode(OK.value());
            assertMenteeMatch(mentorResponse, mentee.id(), MENTEE_1);
            mentorResponse
                    .body("coffeeChat.id", is((int) coffeeChatId))
                    .body("coffeeChat.status", is(APPLY.getValue()))
                    .body("coffeeChat.applyReason", notNullValue(String.class))
                    .body("coffeeChat.question", nullValue())
                    .body("coffeeChat.rejectReason", nullValue())
                    .body("coffeeChat.start", is(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                    .body("coffeeChat.end", is(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                    .body("coffeeChat.chatType", nullValue())
                    .body("coffeeChat.chatValue", nullValue());

            final ValidatableResponse menteeResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentee.token().accessToken()).statusCode(OK.value());
            assertMentorMatch(menteeResponse, mentor.id(), MENTOR_1);
            menteeResponse
                    .body("coffeeChat.id", is((int) coffeeChatId))
                    .body("coffeeChat.status", is(APPLY.getValue()))
                    .body("coffeeChat.applyReason", notNullValue(String.class))
                    .body("coffeeChat.question", nullValue())
                    .body("coffeeChat.rejectReason", nullValue())
                    .body("coffeeChat.start", is(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                    .body("coffeeChat.end", is(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                    .body("coffeeChat.chatType", nullValue())
                    .body("coffeeChat.chatValue", nullValue());
        }

        @Test
        @DisplayName("2. CANCEL 상태 커피챗 상세 조회")
        void cancel() {
            final long coffeeChatId = 멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(start, end, mentor.id(), mentee.token().accessToken());
            신청_제안한_커피챗을_취소한다(coffeeChatId, mentee.token().accessToken());

            final ValidatableResponse mentorResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentor.token().accessToken()).statusCode(OK.value());
            assertMenteeMatch(mentorResponse, mentee.id(), MENTEE_1);
            mentorResponse
                    .body("coffeeChat.id", is((int) coffeeChatId))
                    .body("coffeeChat.status", is(CANCEL.getValue()))
                    .body("coffeeChat.applyReason", notNullValue(String.class))
                    .body("coffeeChat.question", nullValue())
                    .body("coffeeChat.rejectReason", nullValue())
                    .body("coffeeChat.start", is(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                    .body("coffeeChat.end", is(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                    .body("coffeeChat.chatType", nullValue())
                    .body("coffeeChat.chatValue", nullValue());

            final ValidatableResponse menteeResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentee.token().accessToken()).statusCode(OK.value());
            assertMentorMatch(menteeResponse, mentor.id(), MENTOR_1);
            menteeResponse
                    .body("coffeeChat.id", is((int) coffeeChatId))
                    .body("coffeeChat.status", is(CANCEL.getValue()))
                    .body("coffeeChat.applyReason", notNullValue(String.class))
                    .body("coffeeChat.question", nullValue())
                    .body("coffeeChat.rejectReason", nullValue())
                    .body("coffeeChat.start", is(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                    .body("coffeeChat.end", is(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                    .body("coffeeChat.chatType", nullValue())
                    .body("coffeeChat.chatValue", nullValue());
        }

        @Test
        @DisplayName("3. APPROVE 상태 커피챗 상세 조회")
        void approve() {
            final long coffeeChatId = 멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(start, end, mentor.id(), mentee.token().accessToken());
            멘토가_멘티의_커피챗_신청을_수락한다(coffeeChatId, KAKAO_ID, mentor.token().accessToken());

            final ValidatableResponse mentorResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentor.token().accessToken()).statusCode(OK.value());
            assertMenteeMatch(mentorResponse, mentee.id(), MENTEE_1);
            mentorResponse
                    .body("coffeeChat.id", is((int) coffeeChatId))
                    .body("coffeeChat.status", is(APPROVE.getValue()))
                    .body("coffeeChat.applyReason", notNullValue(String.class))
                    .body("coffeeChat.question", nullValue())
                    .body("coffeeChat.rejectReason", nullValue())
                    .body("coffeeChat.start", is(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                    .body("coffeeChat.end", is(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                    .body("coffeeChat.chatType", is(KAKAO_ID.getType().getEng()))
                    .body("coffeeChat.chatValue", is(KAKAO_ID.getValue()));

            final ValidatableResponse menteeResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentee.token().accessToken()).statusCode(OK.value());
            assertMentorMatch(menteeResponse, mentor.id(), MENTOR_1);
            menteeResponse
                    .body("coffeeChat.id", is((int) coffeeChatId))
                    .body("coffeeChat.status", is(APPROVE.getValue()))
                    .body("coffeeChat.applyReason", notNullValue(String.class))
                    .body("coffeeChat.question", nullValue())
                    .body("coffeeChat.rejectReason", nullValue())
                    .body("coffeeChat.start", is(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                    .body("coffeeChat.end", is(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                    .body("coffeeChat.chatType", is(KAKAO_ID.getType().getEng()))
                    .body("coffeeChat.chatValue", is(KAKAO_ID.getValue()));
        }

        @Test
        @DisplayName("4. REJECT 상태 커피챗 상세 조회")
        void reject() {
            final long coffeeChatId = 멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(start, end, mentor.id(), mentee.token().accessToken());
            멘토가_멘티의_커피챗_신청을_거절한다(coffeeChatId, "거절..", mentor.token().accessToken());

            final ValidatableResponse mentorResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentor.token().accessToken()).statusCode(OK.value());
            assertMenteeMatch(mentorResponse, mentee.id(), MENTEE_1);
            mentorResponse
                    .body("coffeeChat.id", is((int) coffeeChatId))
                    .body("coffeeChat.status", is(REJECT.getValue()))
                    .body("coffeeChat.applyReason", notNullValue(String.class))
                    .body("coffeeChat.question", nullValue())
                    .body("coffeeChat.rejectReason", is("거절.."))
                    .body("coffeeChat.start", is(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                    .body("coffeeChat.end", is(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                    .body("coffeeChat.chatType", nullValue())
                    .body("coffeeChat.chatValue", nullValue());

            final ValidatableResponse menteeResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentee.token().accessToken()).statusCode(OK.value());
            assertMentorMatch(menteeResponse, mentor.id(), MENTOR_1);
            menteeResponse
                    .body("coffeeChat.id", is((int) coffeeChatId))
                    .body("coffeeChat.status", is(REJECT.getValue()))
                    .body("coffeeChat.applyReason", notNullValue(String.class))
                    .body("coffeeChat.question", nullValue())
                    .body("coffeeChat.rejectReason", is("거절.."))
                    .body("coffeeChat.start", is(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                    .body("coffeeChat.end", is(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                    .body("coffeeChat.chatType", nullValue())
                    .body("coffeeChat.chatValue", nullValue());
        }
    }

    @Nested
    @DisplayName("내 일정 커피챗 상세 조회를 진행한다 [MentorFlow]")
    class GetCoffeeChatScheduleDetailsWithMentorFlow {
        @Test
        @DisplayName("1. SUGGEST 상태 커피챗 상세 조회")
        void suggest() {
            final long coffeeChatId = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee.id(), mentor.token().accessToken());

            final ValidatableResponse mentorResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentor.token().accessToken()).statusCode(OK.value());
            assertMenteeMatch(mentorResponse, mentee.id(), MENTEE_1);
            mentorResponse
                    .body("coffeeChat.id", is((int) coffeeChatId))
                    .body("coffeeChat.status", is(SUGGEST.getValue()))
                    .body("coffeeChat.applyReason", notNullValue(String.class))
                    .body("coffeeChat.question", nullValue())
                    .body("coffeeChat.rejectReason", nullValue())
                    .body("coffeeChat.start", nullValue())
                    .body("coffeeChat.end", nullValue())
                    .body("coffeeChat.chatType", nullValue())
                    .body("coffeeChat.chatValue", nullValue());

            final ValidatableResponse menteeResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentee.token().accessToken()).statusCode(OK.value());
            assertMentorMatch(menteeResponse, mentor.id(), MENTOR_1);
            menteeResponse
                    .body("coffeeChat.id", is((int) coffeeChatId))
                    .body("coffeeChat.status", is(SUGGEST.getValue()))
                    .body("coffeeChat.applyReason", notNullValue(String.class))
                    .body("coffeeChat.question", nullValue())
                    .body("coffeeChat.rejectReason", nullValue())
                    .body("coffeeChat.start", nullValue())
                    .body("coffeeChat.end", nullValue())
                    .body("coffeeChat.chatType", nullValue())
                    .body("coffeeChat.chatValue", nullValue());
        }

        @Test
        @DisplayName("2. CANCEL 상태 커피챗 상세 조회")
        void cancel() {
            final long coffeeChatId = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee.id(), mentor.token().accessToken());
            신청_제안한_커피챗을_취소한다(coffeeChatId, mentor.token().accessToken());

            final ValidatableResponse mentorResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentor.token().accessToken()).statusCode(OK.value());
            assertMenteeMatch(mentorResponse, mentee.id(), MENTEE_1);
            mentorResponse
                    .body("coffeeChat.id", is((int) coffeeChatId))
                    .body("coffeeChat.status", is(CANCEL.getValue()))
                    .body("coffeeChat.applyReason", notNullValue(String.class))
                    .body("coffeeChat.question", nullValue())
                    .body("coffeeChat.rejectReason", nullValue())
                    .body("coffeeChat.start", nullValue())
                    .body("coffeeChat.end", nullValue())
                    .body("coffeeChat.chatType", nullValue())
                    .body("coffeeChat.chatValue", nullValue());

            final ValidatableResponse menteeResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentee.token().accessToken()).statusCode(OK.value());
            assertMentorMatch(menteeResponse, mentor.id(), MENTOR_1);
            menteeResponse
                    .body("coffeeChat.id", is((int) coffeeChatId))
                    .body("coffeeChat.status", is(CANCEL.getValue()))
                    .body("coffeeChat.applyReason", notNullValue(String.class))
                    .body("coffeeChat.question", nullValue())
                    .body("coffeeChat.rejectReason", nullValue())
                    .body("coffeeChat.start", nullValue())
                    .body("coffeeChat.end", nullValue())
                    .body("coffeeChat.chatType", nullValue())
                    .body("coffeeChat.chatValue", nullValue());
        }

        @Test
        @DisplayName("3. PENDING 상태 커피챗 상세 조회")
        void pending() {
            final long coffeeChatId = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee.id(), mentor.token().accessToken());
            멘티가_멘토의_커피챗_제안을_1차_수락한다(coffeeChatId, start, end, mentee.token().accessToken());

            final ValidatableResponse mentorResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentor.token().accessToken()).statusCode(OK.value());
            assertMenteeMatch(mentorResponse, mentee.id(), MENTEE_1);
            mentorResponse
                    .body("coffeeChat.id", is((int) coffeeChatId))
                    .body("coffeeChat.status", is(PENDING.getValue()))
                    .body("coffeeChat.applyReason", notNullValue(String.class))
                    .body("coffeeChat.question", notNullValue(String.class))
                    .body("coffeeChat.rejectReason", nullValue())
                    .body("coffeeChat.start", is(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                    .body("coffeeChat.end", is(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                    .body("coffeeChat.chatType", nullValue())
                    .body("coffeeChat.chatValue", nullValue());

            final ValidatableResponse menteeResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentee.token().accessToken()).statusCode(OK.value());
            assertMentorMatch(menteeResponse, mentor.id(), MENTOR_1);
            menteeResponse
                    .body("coffeeChat.id", is((int) coffeeChatId))
                    .body("coffeeChat.status", is(PENDING.getValue()))
                    .body("coffeeChat.applyReason", notNullValue(String.class))
                    .body("coffeeChat.question", notNullValue(String.class))
                    .body("coffeeChat.rejectReason", nullValue())
                    .body("coffeeChat.start", is(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                    .body("coffeeChat.end", is(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                    .body("coffeeChat.chatType", nullValue())
                    .body("coffeeChat.chatValue", nullValue());
        }

        @Test
        @DisplayName("4. REJECT(1차) 상태 커피챗 상세 조회")
        void reject() {
            final long coffeeChatId = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee.id(), mentor.token().accessToken());
            멘티가_멘토의_커피챗_제안을_거절한다(coffeeChatId, "거절..", mentee.token().accessToken());

            final ValidatableResponse mentorResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentor.token().accessToken()).statusCode(OK.value());
            assertMenteeMatch(mentorResponse, mentee.id(), MENTEE_1);
            mentorResponse
                    .body("coffeeChat.id", is((int) coffeeChatId))
                    .body("coffeeChat.status", is(REJECT.getValue()))
                    .body("coffeeChat.applyReason", notNullValue(String.class))
                    .body("coffeeChat.question", nullValue())
                    .body("coffeeChat.rejectReason", notNullValue(String.class))
                    .body("coffeeChat.start", nullValue())
                    .body("coffeeChat.end", nullValue())
                    .body("coffeeChat.chatType", nullValue())
                    .body("coffeeChat.chatValue", nullValue());

            final ValidatableResponse menteeResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentee.token().accessToken()).statusCode(OK.value());
            assertMentorMatch(menteeResponse, mentor.id(), MENTOR_1);
            menteeResponse
                    .body("coffeeChat.id", is((int) coffeeChatId))
                    .body("coffeeChat.status", is(REJECT.getValue()))
                    .body("coffeeChat.applyReason", notNullValue(String.class))
                    .body("coffeeChat.question", nullValue())
                    .body("coffeeChat.rejectReason", notNullValue(String.class))
                    .body("coffeeChat.start", nullValue())
                    .body("coffeeChat.end", nullValue())
                    .body("coffeeChat.chatType", nullValue())
                    .body("coffeeChat.chatValue", nullValue());
        }

        @Test
        @DisplayName("5. APPROVE 상태 커피챗 상세 조회")
        void finallyApprove() {
            final long coffeeChatId = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee.id(), mentor.token().accessToken());
            멘티가_멘토의_커피챗_제안을_1차_수락한다(coffeeChatId, start, end, mentee.token().accessToken());
            멘토가_Pending_상태인_커피챗에_대해서_최종_수락을_한다(coffeeChatId, KAKAO_ID, mentor.token().accessToken());

            final ValidatableResponse mentorResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentor.token().accessToken()).statusCode(OK.value());
            assertMenteeMatch(mentorResponse, mentee.id(), MENTEE_1);
            mentorResponse
                    .body("coffeeChat.id", is((int) coffeeChatId))
                    .body("coffeeChat.status", is(APPROVE.getValue()))
                    .body("coffeeChat.applyReason", notNullValue(String.class))
                    .body("coffeeChat.question", notNullValue(String.class))
                    .body("coffeeChat.rejectReason", nullValue())
                    .body("coffeeChat.start", is(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                    .body("coffeeChat.end", is(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                    .body("coffeeChat.chatType", is(KAKAO_ID.getType().getEng()))
                    .body("coffeeChat.chatValue", is(KAKAO_ID.getValue()));

            final ValidatableResponse menteeResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentee.token().accessToken()).statusCode(OK.value());
            assertMentorMatch(menteeResponse, mentor.id(), MENTOR_1);
            menteeResponse
                    .body("coffeeChat.id", is((int) coffeeChatId))
                    .body("coffeeChat.status", is(APPROVE.getValue()))
                    .body("coffeeChat.applyReason", notNullValue(String.class))
                    .body("coffeeChat.question", notNullValue(String.class))
                    .body("coffeeChat.rejectReason", nullValue())
                    .body("coffeeChat.start", is(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                    .body("coffeeChat.end", is(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                    .body("coffeeChat.chatType", is(KAKAO_ID.getType().getEng()))
                    .body("coffeeChat.chatValue", is(KAKAO_ID.getValue()));
        }

        @Test
        @DisplayName("6. REJECT(최종) 상태 커피챗 상세 조회")
        void finallyReject() {
            final long coffeeChatId = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee.id(), mentor.token().accessToken());
            멘티가_멘토의_커피챗_제안을_1차_수락한다(coffeeChatId, start, end, mentee.token().accessToken());
            멘토가_Pending_상태인_커피챗에_대해서_최종_거절을_한다(coffeeChatId, "거절..", mentor.token().accessToken());

            final ValidatableResponse mentorResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentor.token().accessToken()).statusCode(OK.value());
            assertMenteeMatch(mentorResponse, mentee.id(), MENTEE_1);
            mentorResponse
                    .body("coffeeChat.id", is((int) coffeeChatId))
                    .body("coffeeChat.status", is(REJECT.getValue()))
                    .body("coffeeChat.applyReason", notNullValue(String.class))
                    .body("coffeeChat.question", notNullValue(String.class))
                    .body("coffeeChat.rejectReason", notNullValue(String.class))
                    .body("coffeeChat.start", is(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                    .body("coffeeChat.end", is(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                    .body("coffeeChat.chatType", nullValue())
                    .body("coffeeChat.chatValue", nullValue());

            final ValidatableResponse menteeResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentee.token().accessToken()).statusCode(OK.value());
            assertMentorMatch(menteeResponse, mentor.id(), MENTOR_1);
            menteeResponse
                    .body("coffeeChat.id", is((int) coffeeChatId))
                    .body("coffeeChat.status", is(REJECT.getValue()))
                    .body("coffeeChat.applyReason", notNullValue(String.class))
                    .body("coffeeChat.question", notNullValue(String.class))
                    .body("coffeeChat.rejectReason", notNullValue(String.class))
                    .body("coffeeChat.start", is(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                    .body("coffeeChat.end", is(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                    .body("coffeeChat.chatType", nullValue())
                    .body("coffeeChat.chatValue", nullValue());
        }
    }

    private void assertMentorMatch(
            final ValidatableResponse response,
            final Long id,
            final MentorFixture mentor
    ) {
        response
                .body("mentor.id", is(id.intValue()))
                .body("mentor.name", is(mentor.getName()))
                .body("mentor.profileImageUrl", is(mentor.getProfileImageUrl()))
                .body("mentor.introduction", is(mentor.getIntroduction()))
                .body("mentor.languages.main", is(KR.getCode()))
                .body("mentor.languages.sub", containsInAnyOrder(List.of(EN.getCode()).toArray()))
                .body("mentor.school", is(mentor.getUniversityProfile().getSchool()))
                .body("mentor.major", is(mentor.getUniversityProfile().getMajor()))
                .body("mentor.enteredIn", is(mentor.getUniversityProfile().getEnteredIn()));
    }

    private void assertMenteeMatch(
            final ValidatableResponse response,
            final Long id,
            final MenteeFixture mentee
    ) {
        response
                .body("mentee.id", is(id.intValue()))
                .body("mentee.name", is(mentee.getName()))
                .body("mentee.profileImageUrl", is(mentee.getProfileImageUrl()))
                .body("mentee.nationality", is(mentee.getNationality().getCode()))
                .body("mentee.introduction", is(mentee.getIntroduction()))
                .body("mentee.languages.main", is(EN.getCode()))
                .body("mentee.languages.sub", containsInAnyOrder(List.of(KR.getCode()).toArray()))
                .body("mentee.interestSchool", is(mentee.getInterest().getSchool()))
                .body("mentee.interestMajor", is(mentee.getInterest().getMajor()));
    }
}
