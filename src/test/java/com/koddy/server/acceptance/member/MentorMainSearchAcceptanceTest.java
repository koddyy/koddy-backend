package com.koddy.server.acceptance.member;

import com.koddy.server.auth.domain.model.AuthMember;
import com.koddy.server.common.AcceptanceTest;
import com.koddy.server.common.containers.callback.DatabaseCleanerAllCallbackExtension;
import com.koddy.server.common.fixture.MenteeFixture;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다;
import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.신청_제안한_커피챗을_취소한다;
import static com.koddy.server.acceptance.member.MemberAcceptanceStep.멘티들을_둘러본다;
import static com.koddy.server.acceptance.member.MemberAcceptanceStep.커피챗_신청한_멘티를_조회한다;
import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_PERMISSION;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_10;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_11;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_12;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_13;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_14;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_15;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_16;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_17;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_18;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_19;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_2;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_20;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_3;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_4;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_5;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_6;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_7;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_8;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_9;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(DatabaseCleanerAllCallbackExtension.class)
@DisplayName("[Acceptance Test] 멘토 메인 홈 조회 - 신청온 커피챗, 멘티 둘러보기")
public class MentorMainSearchAcceptanceTest extends AcceptanceTest {
    private static AuthMember mentor;
    private static final AuthMember[] mentees = new AuthMember[20];

    @BeforeAll
    static void setUp() {
        mentor = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다();

        final List<MenteeFixture> fixtures = Arrays.stream(MenteeFixture.values())
                .limit(20)
                .toList();
        Arrays.setAll(mentees, it -> fixtures.get(it).회원가입과_로그인을_하고_프로필을_완성시킨다());
    }

    @Nested
    @DisplayName("멘티로부터 신청온 커피챗 조회 API")
    class GetAppliedMentees {
        @Test
        @DisplayName("멘토가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() {
            커피챗_신청한_멘티를_조회한다(mentees[0].token().accessToken())
                    .statusCode(FORBIDDEN.value())
                    .body("errorCode", is(INVALID_PERMISSION.getErrorCode()))
                    .body("message", is(INVALID_PERMISSION.getMessage()));
        }

        @Test
        @DisplayName("멘티로부터 신청온 커피챗을 조회한다 [Limit 3]")
        void success() {
            final long coffeeChatId1 = 멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(
                    LocalDateTime.of(2024, 2, 5, 18, 0),
                    LocalDateTime.of(2024, 2, 5, 18, 30),
                    mentor.id(),
                    mentees[0].token().accessToken()
            );
            final long coffeeChatId2 = 멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(
                    LocalDateTime.of(2024, 2, 7, 18, 0),
                    LocalDateTime.of(2024, 2, 7, 18, 30),
                    mentor.id(),
                    mentees[1].token().accessToken()
            );
            final long coffeeChatId3 = 멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(
                    LocalDateTime.of(2024, 2, 9, 18, 0),
                    LocalDateTime.of(2024, 2, 9, 18, 30),
                    mentor.id(),
                    mentees[2].token().accessToken()
            );
            final long coffeeChatId4 = 멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(
                    LocalDateTime.of(2024, 2, 12, 18, 0),
                    LocalDateTime.of(2024, 2, 12, 18, 30),
                    mentor.id(),
                    mentees[3].token().accessToken()
            );
            final long coffeeChatId5 = 멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(
                    LocalDateTime.of(2024, 2, 14, 18, 0),
                    LocalDateTime.of(2024, 2, 14, 18, 30),
                    mentor.id(),
                    mentees[4].token().accessToken()
            );

            /* 5명 신청 */
            final ValidatableResponse response1 = 커피챗_신청한_멘티를_조회한다(mentor.token().accessToken())
                    .statusCode(OK.value());
            assertMenteesMatch(
                    response1,
                    List.of(coffeeChatId5, coffeeChatId4, coffeeChatId3),
                    List.of(mentees[4].id(), mentees[3].id(), mentees[2].id()),
                    List.of(MENTEE_5, MENTEE_4, MENTEE_3),
                    5L,
                    true
            );

            /* 2명 취소 */
            신청_제안한_커피챗을_취소한다(coffeeChatId4, mentees[3].token().accessToken());
            신청_제안한_커피챗을_취소한다(coffeeChatId2, mentees[1].token().accessToken());

            final ValidatableResponse response2 = 커피챗_신청한_멘티를_조회한다(mentor.token().accessToken())
                    .statusCode(OK.value());
            assertMenteesMatch(
                    response2,
                    List.of(coffeeChatId5, coffeeChatId3, coffeeChatId1),
                    List.of(mentees[4].id(), mentees[2].id(), mentees[0].id()),
                    List.of(MENTEE_5, MENTEE_3, MENTEE_1),
                    3L,
                    false
            );
        }
    }

    @Nested
    @DisplayName("멘티 둘러보기 API")
    class GetMenteesByCondition {
        private static final String BASE_URL = "/api/mentees";

        @Test
        @DisplayName("멘티 둘러보기를 진행한다")
        void success() {
            /* 최신 가입순 */
            final ValidatableResponse response1 = 멘티들을_둘러본다(BASE_URL + "?page=1").statusCode(OK.value());
            assertMenteesMatch(
                    response1,
                    List.of(
                            mentees[19].id(), mentees[18].id(), mentees[17].id(), mentees[16].id(), mentees[15].id(),
                            mentees[14].id(), mentees[13].id(), mentees[12].id(), mentees[11].id(), mentees[10].id()
                    ),
                    List.of(
                            MENTEE_20, MENTEE_19, MENTEE_18, MENTEE_17, MENTEE_16,
                            MENTEE_15, MENTEE_14, MENTEE_13, MENTEE_12, MENTEE_11
                    ),
                    true
            );

            final ValidatableResponse response2 = 멘티들을_둘러본다(BASE_URL + "?page=2").statusCode(OK.value());
            assertMenteesMatch(
                    response2,
                    List.of(
                            mentees[9].id(), mentees[8].id(), mentees[7].id(), mentees[6].id(), mentees[5].id(),
                            mentees[4].id(), mentees[3].id(), mentees[2].id(), mentees[1].id(), mentees[0].id()
                    ),
                    List.of(
                            MENTEE_10, MENTEE_9, MENTEE_8, MENTEE_7, MENTEE_6,
                            MENTEE_5, MENTEE_4, MENTEE_3, MENTEE_2, MENTEE_1
                    ),
                    false
            );

            /* 최신 가입순 + 국적 */
            final ValidatableResponse response3 = 멘티들을_둘러본다(BASE_URL + "?page=1&nationalities=EN,JP,CN").statusCode(OK.value());
            assertMenteesMatch(
                    response3,
                    List.of(
                            mentees[17].id(), mentees[16].id(), mentees[15].id(), mentees[12].id(), mentees[11].id(),
                            mentees[10].id(), mentees[7].id(), mentees[6].id(), mentees[5].id(), mentees[2].id()
                    ),
                    List.of(
                            MENTEE_18, MENTEE_17, MENTEE_16, MENTEE_13, MENTEE_12,
                            MENTEE_11, MENTEE_8, MENTEE_7, MENTEE_6, MENTEE_3
                    ),
                    true
            );

            final ValidatableResponse response4 = 멘티들을_둘러본다(BASE_URL + "?page=2&nationalities=EN,JP,CN").statusCode(OK.value());
            assertMenteesMatch(
                    response4,
                    List.of(mentees[1].id(), mentees[0].id()),
                    List.of(MENTEE_2, MENTEE_1),
                    false
            );

            /* 최신 가입순 + 언어 */
            final ValidatableResponse response5 = 멘티들을_둘러본다(BASE_URL + "?page=1&languages=EN,KR").statusCode(OK.value());
            assertMenteesMatch(
                    response5,
                    List.of(
                            mentees[18].id(), mentees[16].id(), mentees[14].id(), mentees[12].id(), mentees[10].id(),
                            mentees[8].id(), mentees[6].id(), mentees[4].id(), mentees[2].id(), mentees[0].id()
                    ),
                    List.of(
                            MENTEE_19, MENTEE_17, MENTEE_15, MENTEE_13, MENTEE_11,
                            MENTEE_9, MENTEE_7, MENTEE_5, MENTEE_3, MENTEE_1
                    ),
                    false
            );

            final ValidatableResponse response6 = 멘티들을_둘러본다(BASE_URL + "?page=2&languages=KR").statusCode(OK.value());
            assertMenteesMatch(
                    response6,
                    List.of(),
                    List.of(),
                    false
            );

            /* 최신 가입순 + 국적 + 언어 */
            final ValidatableResponse response7 = 멘티들을_둘러본다(BASE_URL + "?page=1&nationalities=EN,JP,CN&languages=EN,KR").statusCode(OK.value());
            assertMenteesMatch(
                    response7,
                    List.of(mentees[16].id(), mentees[12].id(), mentees[10].id(), mentees[6].id(), mentees[2].id(), mentees[0].id()),
                    List.of(MENTEE_17, MENTEE_13, MENTEE_11, MENTEE_7, MENTEE_3, MENTEE_1),
                    false
            );

            final ValidatableResponse response8 = 멘티들을_둘러본다(BASE_URL + "?page=2&nationalities=EN,JP,CN&languages=EN,KR").statusCode(OK.value());
            assertMenteesMatch(
                    response8,
                    List.of(),
                    List.of(),
                    false
            );
        }
    }

    private void assertMenteesMatch(
            final ValidatableResponse response,
            final List<Long> coffeeChatIds,
            final List<Long> menteeIds,
            final List<MenteeFixture> mentees,
            final Long totalCount,
            final boolean hasNext
    ) {
        final int totalSize = mentees.size();
        response
                .body("result", hasSize(totalSize))
                .body("totalCount", is(totalCount.intValue()))
                .body("hasNext", is(hasNext));

        for (int i = 0; i < totalSize; i++) {
            final String index = String.format("result[%d]", i);
            final Long coffeeChatId = coffeeChatIds.get(i);
            final Long menteeId = menteeIds.get(i);
            final MenteeFixture mentee = mentees.get(i);

            response
                    .body(index + ".coffeeChatId", is(coffeeChatId.intValue()))
                    .body(index + ".menteeId", is(menteeId.intValue()))
                    .body(index + ".name", is(mentee.getName()))
                    .body(index + ".profileImageUrl", is(mentee.getProfileImageUrl()))
                    .body(index + ".nationality", is(mentee.getNationality().code))
                    .body(index + ".interestSchool", is(mentee.getInterest().getSchool()))
                    .body(index + ".interestMajor", is(mentee.getInterest().getMajor()));
        }
    }

    private void assertMenteesMatch(
            final ValidatableResponse response,
            final List<Long> menteeIds,
            final List<MenteeFixture> mentees,
            final boolean hasNext
    ) {
        final int totalSize = mentees.size();
        response
                .body("result", hasSize(totalSize))
                .body("hasNext", is(hasNext));

        for (int i = 0; i < totalSize; i++) {
            final String index = String.format("result[%d]", i);
            final Long menteeId = menteeIds.get(i);
            final MenteeFixture mentee = mentees.get(i);

            response
                    .body(index + ".id", is(menteeId.intValue()))
                    .body(index + ".name", is(mentee.getName()))
                    .body(index + ".profileImageUrl", is(mentee.getProfileImageUrl()))
                    .body(index + ".nationality", is(mentee.getNationality().code))
                    .body(index + ".interestSchool", is(mentee.getInterest().getSchool()))
                    .body(index + ".interestMajor", is(mentee.getInterest().getMajor()));
        }
    }
}
