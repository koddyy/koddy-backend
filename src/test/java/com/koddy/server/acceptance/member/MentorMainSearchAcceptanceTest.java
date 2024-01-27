package com.koddy.server.acceptance.member;

import com.koddy.server.auth.domain.model.AuthMember;
import com.koddy.server.auth.domain.model.AuthToken;
import com.koddy.server.common.AcceptanceTest;
import com.koddy.server.common.containers.callback.DatabaseCleanerAllCallbackExtension;
import com.koddy.server.common.fixture.MenteeFixture;
import com.koddy.server.common.fixture.MentorFixture;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.presentation.dto.request.LanguageRequest;
import com.koddy.server.member.presentation.dto.request.SignUpMenteeRequest;
import com.koddy.server.member.presentation.dto.request.SignUpMentorRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다;
import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.신청_제안한_커피챗을_취소한다;
import static com.koddy.server.acceptance.member.MemberAcceptanceStep.멘티들을_둘러본다;
import static com.koddy.server.acceptance.member.MemberAcceptanceStep.커피챗_신청한_멘티를_조회한다;
import static com.koddy.server.auth.domain.model.AuthToken.ACCESS_TOKEN_HEADER;
import static com.koddy.server.auth.domain.model.AuthToken.REFRESH_TOKEN_HEADER;
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
@DisplayName("[Acceptance Test] 멘토 메인 홈 조회 - 커피챗 신청한 멘티, 멘티 둘러보기")
public class MentorMainSearchAcceptanceTest extends AcceptanceTest {
    private static AuthMember mentor;
    private static final AuthMember[] mentees = new AuthMember[20];

    @BeforeAll
    static void setUp() {
        mentor = createMentor(MENTOR_1);

        final List<MenteeFixture> fixtures = Arrays.stream(MenteeFixture.values())
                .limit(20)
                .toList();
        Arrays.setAll(mentees, it -> createMentee(fixtures.get(it)));
    }

    @Nested
    @DisplayName("커피챗 신청한 멘티 조회 API")
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
        @DisplayName("커피챗을 신청한 멘티들을 조회한다 [Limit 3]")
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
                    List.of(MENTEE_5, MENTEE_4, MENTEE_3),
                    List.of(mentees[4].id(), mentees[3].id(), mentees[2].id())
            );

            /* 3명 취소 */
            신청_제안한_커피챗을_취소한다(coffeeChatId4, mentees[3].token().accessToken());
            신청_제안한_커피챗을_취소한다(coffeeChatId2, mentees[1].token().accessToken());

            final ValidatableResponse response2 = 커피챗_신청한_멘티를_조회한다(mentor.token().accessToken())
                    .statusCode(OK.value());
            assertMenteesMatch(
                    response2,
                    List.of(MENTEE_5, MENTEE_3, MENTEE_1),
                    List.of(mentees[4].id(), mentees[2].id(), mentees[0].id())
            );
        }
    }

    @Nested
    @DisplayName("멘티 둘러보기 API")
    class GetMenteesByCondition {
        @Test
        @DisplayName("멘티 둘러보기를 진행한다")
        void success() {
            /* 최신 가입순 */
            final String url1 = UriComponentsBuilder
                    .fromUriString("/api/mentees?page=1")
                    .build()
                    .toUriString();
            final ValidatableResponse response1 = 멘티들을_둘러본다(url1).statusCode(OK.value());
            assertMenteesMatch(
                    response1,
                    List.of(
                            MENTEE_20, MENTEE_19, MENTEE_18, MENTEE_17, MENTEE_16,
                            MENTEE_15, MENTEE_14, MENTEE_13, MENTEE_12, MENTEE_11
                    ),
                    List.of(
                            mentees[19].id(), mentees[18].id(), mentees[17].id(), mentees[16].id(), mentees[15].id(),
                            mentees[14].id(), mentees[13].id(), mentees[12].id(), mentees[11].id(), mentees[10].id()
                    )
            );
            response1.body("hasNext", is(true));

            final String url2 = UriComponentsBuilder
                    .fromUriString("/api/mentees?page=2")
                    .build()
                    .toUriString();
            final ValidatableResponse response2 = 멘티들을_둘러본다(url2).statusCode(OK.value());
            assertMenteesMatch(
                    response2,
                    List.of(
                            MENTEE_10, MENTEE_9, MENTEE_8, MENTEE_7, MENTEE_6,
                            MENTEE_5, MENTEE_4, MENTEE_3, MENTEE_2, MENTEE_1
                    ),
                    List.of(
                            mentees[9].id(), mentees[8].id(), mentees[7].id(), mentees[6].id(), mentees[5].id(),
                            mentees[4].id(), mentees[3].id(), mentees[2].id(), mentees[1].id(), mentees[0].id()
                    )
            );
            response2.body("hasNext", is(false));

            /* 최신 가입순 + 국적 */
            final String url3 = UriComponentsBuilder
                    .fromUriString("/api/mentees?page=1&nationalities=EN&nationalities=JP&nationalities=CN")
                    .build()
                    .toUriString();
            final ValidatableResponse response3 = 멘티들을_둘러본다(url3).statusCode(OK.value());
            assertMenteesMatch(
                    response3,
                    List.of(
                            MENTEE_18, MENTEE_17, MENTEE_16, MENTEE_13, MENTEE_12,
                            MENTEE_11, MENTEE_8, MENTEE_7, MENTEE_6, MENTEE_3
                    ),
                    List.of(
                            mentees[17].id(), mentees[16].id(), mentees[15].id(), mentees[12].id(), mentees[11].id(),
                            mentees[10].id(), mentees[7].id(), mentees[6].id(), mentees[5].id(), mentees[2].id()
                    )
            );
            response3.body("hasNext", is(true));

            final String url4 = UriComponentsBuilder
                    .fromUriString("/api/mentees?page=2&nationalities=EN&nationalities=JP&nationalities=CN")
                    .build()
                    .toUriString();
            final ValidatableResponse response4 = 멘티들을_둘러본다(url4).statusCode(OK.value());
            assertMenteesMatch(
                    response4,
                    List.of(MENTEE_2, MENTEE_1),
                    List.of(mentees[1].id(), mentees[0].id())
            );
            response4.body("hasNext", is(false));

            /* 최신 가입순 + 언어 */
            final String url5 = UriComponentsBuilder
                    .fromUriString("/api/mentees?page=1&languages=EN&languages=KR")
                    .build()
                    .toUriString();
            final ValidatableResponse response5 = 멘티들을_둘러본다(url5).statusCode(OK.value());
            assertMenteesMatch(
                    response5,
                    List.of(
                            MENTEE_19, MENTEE_17, MENTEE_15, MENTEE_13, MENTEE_11,
                            MENTEE_9, MENTEE_7, MENTEE_5, MENTEE_3, MENTEE_1
                    ),
                    List.of(
                            mentees[18].id(), mentees[16].id(), mentees[14].id(), mentees[12].id(), mentees[10].id(),
                            mentees[8].id(), mentees[6].id(), mentees[4].id(), mentees[2].id(), mentees[0].id()
                    )
            );
            response5.body("hasNext", is(false));

            final String url6 = UriComponentsBuilder
                    .fromUriString("/api/mentees?page=2&languages=KR")
                    .build()
                    .toUriString();
            final ValidatableResponse response6 = 멘티들을_둘러본다(url6).statusCode(OK.value());
            assertMenteesMatch(
                    response6,
                    List.of(),
                    List.of()
            );
            response6.body("hasNext", is(false));

            /* 최신 가입순 + 국적 + 언어 */
            final String url7 = UriComponentsBuilder
                    .fromUriString("/api/mentees?page=1&nationalities=EN&nationalities=JP&nationalities=CN&languages=EN&languages=KR")
                    .build()
                    .toUriString();
            final ValidatableResponse response7 = 멘티들을_둘러본다(url7).statusCode(OK.value());
            assertMenteesMatch(
                    response7,
                    List.of(MENTEE_17, MENTEE_13, MENTEE_11, MENTEE_7, MENTEE_3, MENTEE_1),
                    List.of(mentees[16].id(), mentees[12].id(), mentees[10].id(), mentees[6].id(), mentees[2].id(), mentees[0].id())
            );
            response7.body("hasNext", is(false));

            final String url8 = UriComponentsBuilder
                    .fromUriString("/api/mentees?page=2&nationalities=EN&nationalities=JP&nationalities=CN&languages=EN&languages=KR")
                    .build()
                    .toUriString();
            final ValidatableResponse response8 = 멘티들을_둘러본다(url8).statusCode(OK.value());
            assertMenteesMatch(
                    response8,
                    List.of(),
                    List.of()
            );
            response8.body("hasNext", is(false));
        }
    }

    private void assertMenteesMatch(
            final ValidatableResponse response,
            final List<MenteeFixture> mentees,
            final List<Long> ids
    ) {
        final int totalCount = mentees.size();
        response
                .body("result", hasSize(totalCount));

        for (int i = 0; i < totalCount; i++) {
            final String index = String.format("result[%d]", i);
            final MenteeFixture mentee = mentees.get(i);
            final Long id = ids.get(i);

            response
                    .body(index + ".id", is(id.intValue()))
                    .body(index + ".name", is(mentee.getName()))
                    .body(index + ".profileImageUrl", is(mentee.getProfileImageUrl()))
                    .body(index + ".nationality", is(mentee.getNationality().getValue()))
                    .body(index + ".interestSchool", is(mentee.getInterest().getSchool()))
                    .body(index + ".interestMajor", is(mentee.getInterest().getMajor()));
        }
    }

    private static AuthMember createMentor(final MentorFixture fixture) {
        final SignUpMentorRequest request = new SignUpMentorRequest(
                fixture.getPlatform().getProvider().getValue(),
                fixture.getPlatform().getSocialId(),
                fixture.getPlatform().getEmail().getValue(),
                fixture.getName(),
                fixture.getProfileImageUrl(),
                new LanguageRequest(
                        fixture.getLanguages()
                                .stream()
                                .filter(it -> it.getType() == Language.Type.MAIN)
                                .toList()
                                .get(0)
                                .getCategory()
                                .getCode(),
                        fixture.getLanguages()
                                .stream()
                                .filter(it -> it.getType() == Language.Type.SUB)
                                .map(it -> it.getCategory().getCode())
                                .toList()
                ),
                fixture.getUniversityProfile().getSchool(),
                fixture.getUniversityProfile().getMajor(),
                fixture.getUniversityProfile().getEnteredIn()
        );

        final ExtractableResponse<Response> result = MemberAcceptanceStep.멘토_회원가입_후_로그인을_진행한다(request).extract();
        final long memberId = result.jsonPath().getLong("id");
        final String accessToken = result.header(ACCESS_TOKEN_HEADER).split(" ")[1];
        final String refreshToken = result.cookie(REFRESH_TOKEN_HEADER);

        MemberAcceptanceStep.멘토_프로필을_완성시킨다(fixture, accessToken);
        return new AuthMember(
                memberId,
                fixture.getName(),
                fixture.getProfileImageUrl(),
                new AuthToken(accessToken, refreshToken)
        );
    }

    private static AuthMember createMentee(final MenteeFixture fixture) {
        final SignUpMenteeRequest request = new SignUpMenteeRequest(
                fixture.getPlatform().getProvider().getValue(),
                fixture.getPlatform().getSocialId(),
                fixture.getPlatform().getEmail().getValue(),
                fixture.getName(),
                fixture.getProfileImageUrl(),
                fixture.getNationality().getCode(),
                new LanguageRequest(
                        fixture.getLanguages()
                                .stream()
                                .filter(it -> it.getType() == Language.Type.MAIN)
                                .toList()
                                .get(0)
                                .getCategory()
                                .getCode(),
                        fixture.getLanguages()
                                .stream()
                                .filter(it -> it.getType() == Language.Type.SUB)
                                .map(it -> it.getCategory().getCode())
                                .toList()
                ),
                fixture.getInterest().getSchool(),
                fixture.getInterest().getMajor()
        );

        final ExtractableResponse<Response> result = MemberAcceptanceStep.멘티_회원가입_후_로그인을_진행한다(request).extract();
        final long memberId = result.jsonPath().getLong("id");
        final String accessToken = result.header(ACCESS_TOKEN_HEADER).split(" ")[1];
        final String refreshToken = result.cookie(REFRESH_TOKEN_HEADER);

        MemberAcceptanceStep.멘티_프로필을_완성시킨다(fixture, accessToken);
        return new AuthMember(
                memberId,
                fixture.getName(),
                fixture.getProfileImageUrl(),
                new AuthToken(accessToken, refreshToken)
        );
    }
}
