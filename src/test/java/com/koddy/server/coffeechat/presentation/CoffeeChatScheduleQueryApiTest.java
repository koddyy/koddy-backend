//package com.koddy.server.coffeechat.presentation;
//
//import com.koddy.server.coffeechat.application.usecase.GetCoffeeChatScheduleUseCase;
//import com.koddy.server.coffeechat.application.usecase.query.response.CoffeeChatEachCategoryCounts;
//import com.koddy.server.coffeechat.domain.repository.query.response.MenteeCoffeeChatScheduleData;
//import com.koddy.server.coffeechat.domain.repository.query.response.MentorCoffeeChatScheduleData;
//import com.koddy.server.common.ApiDocsTest;
//import com.koddy.server.global.query.SliceResponse;
//import com.koddy.server.member.domain.model.mentee.Mentee;
//import com.koddy.server.member.domain.model.mentor.Mentor;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.List;
//import java.util.Map;
//
//import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_APPLY;
//import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
//import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
//import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.body;
//import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.query;
//import static com.koddy.server.common.utils.RestDocsSpecificationUtils.createHttpSpecSnippets;
//import static com.koddy.server.common.utils.RestDocsSpecificationUtils.successDocsWithAccessToken;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
//import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@DisplayName("CoffeeChat -> CoffeeChatScheduleQueryApi 테스트")
//public class CoffeeChatScheduleQueryApiTest extends ApiDocsTest {
//    @Autowired
//    private GetCoffeeChatScheduleUseCase getCoffeeChatScheduleUseCase;
//
//    private final Mentor mentor = MENTOR_1.toDomain().apply(1L);
//    private final Mentee mentee = MENTEE_1.toDomain().apply(2L);
//
//    @Nested
//    @DisplayName("내 일정 상태별 커피챗 개수 조회 API [GET /api/coffeechats/me/category-counts]")
//    class GetEachCategoryCounts {
//        private static final String BASE_URL = "/api/coffeechats/me/category-counts";
//
//        @Test
//        @DisplayName("내 일정 상태별 커피챗 개수를 조회한다")
//        void success() {
//            // given
//            applyToken(true, mentor);
//            given(getCoffeeChatScheduleUseCase.getEachCategoryCounts(any())).willReturn(new CoffeeChatEachCategoryCounts(3L, 1L, 0L, 2L));
//
//            // when - then
//            successfulExecute(
//                    getRequestWithAccessToken(BASE_URL),
//                    status().isOk(),
//                    successDocsWithAccessToken("CoffeeChatApi/MySchedule/CategoryCounts", createHttpSpecSnippets(
//                            responseFields(
//                                    body("waiting", "대기 일정 개수"),
//                                    body("suggested", "제안 일정 개수"),
//                                    body("scheduled", "예정 일정 개수"),
//                                    body("passed", "지나간 일정 개수")
//                            )
//                    ))
//            );
//        }
//    }
//
//    @Nested
//    @DisplayName("내 일정 상태별 커피챗 정보 조회 API [GET /api/coffeechats/me/schedules]")
//    class GetSchedules {
//        private static final String BASE_URL = "/api/coffeechats/me/schedules";
//
//        @Test
//        @DisplayName("멘토의 내 일정에서 상태별 커피챗 정보를 조회한다")
//        void mentor() {
//            // given
//            applyToken(true, mentor);
//            given(getCoffeeChatScheduleUseCase.getMentorSchedules(any())).willReturn(new SliceResponse<>(
//                    List.of(new MentorCoffeeChatScheduleData(
//                            1L,
//                            MENTEE_APPLY.name(),
//                            mentee.getId(),
//                            mentee.getName(),
//                            mentee.getProfileImageUrl(),
//                            mentee.getInterest().getSchool(),
//                            mentee.getInterest().getMajor()
//                    )),
//                    false
//            ));
//
//            // when - then
//            successfulExecute(
//                    getRequestWithAccessToken(BASE_URL, Map.of(
//                            "category", "waiting",
//                            "detail", "apply",
//                            "page", "1"
//                    )),
//                    status().isOk(),
//                    successDocsWithAccessToken("CoffeeChatApi/MySchedule/Mentor", createHttpSpecSnippets(
//                            queryParameters(
//                                    query(
//                                            "category",
//                                            "카테고리 필터",
//                                            "- 대기 = waiting" + ENTER
//                                                    + "- 제안 = suggested" + ENTER
//                                                    + "- 예정 = scheduled" + ENTER
//                                                    + "- 지나감 = passed" + ENTER,
//                                            true
//                                    ),
//                                    query(
//                                            "detail",
//                                            "상세 필터",
//                                            "[안보내면 전체]" + ENTER
//                                                    + "- 신청(with 대기) = apply" + ENTER
//                                                    + "- 수락(with 대기) = pending" + ENTER
//                                                    + "- 예정(with 예정) = approve" + ENTER
//                                                    + "- 취소(with 지나감) = cancel" + ENTER
//                                                    + "- 거절(with 지나감) = reject" + ENTER
//                                                    + "- 완료(with 지나감) = complete" + ENTER,
//                                            false
//                                    ),
//                                    query("page", "페이지", "1부터 시작", true)
//                            ),
//                            responseFields(
//                                    body("result[].id", "커피챗 ID(PK)"),
//                                    body("result[].status", "커피챗 상태"),
//                                    body("result[].menteeId", "멘티 ID(PK)"),
//                                    body("result[].name", "멘티 이름"),
//                                    body("result[].profileImageUrl", "멘티 프로필 이미지 URL", "Nullable"),
//                                    body("result[].interestSchool", "멘티 관심있는 학교"),
//                                    body("result[].interestMajor", "멘티 관심있는 전공"),
//                                    body("hasNext", "다음 스크롤 존재 여부")
//                            )
//                    ))
//            );
//        }
//
//        @Test
//        @DisplayName("멘티의 내 일정에서 상태별 커피챗 정보를 조회한다")
//        void mentee() {
//            // given
//            applyToken(true, mentee);
//            given(getCoffeeChatScheduleUseCase.getMenteeSchedules(any())).willReturn(new SliceResponse<>(
//                    List.of(new MenteeCoffeeChatScheduleData(
//                            1L,
//                            MENTEE_APPLY.name(),
//                            mentor.getId(),
//                            mentor.getName(),
//                            mentor.getProfileImageUrl(),
//                            mentor.getUniversityProfile().getSchool(),
//                            mentor.getUniversityProfile().getMajor(),
//                            mentor.getUniversityProfile().getEnteredIn()
//                    )),
//                    false
//            ));
//
//            // when - then
//            successfulExecute(
//                    getRequestWithAccessToken(BASE_URL, Map.of(
//                            "category", "waiting",
//                            "detail", "apply",
//                            "page", "1"
//                    )),
//                    status().isOk(),
//                    successDocsWithAccessToken("CoffeeChatApi/MySchedule/Mentee", createHttpSpecSnippets(
//                            queryParameters(
//                                    query(
//                                            "category",
//                                            "카테고리 필터",
//                                            "- 대기 = waiting" + ENTER
//                                                    + "- 제안 = suggested" + ENTER
//                                                    + "- 예정 = scheduled" + ENTER
//                                                    + "- 지나감 = passed" + ENTER,
//                                            true
//                                    ),
//                                    query(
//                                            "detail",
//                                            "상세 필터",
//                                            "[안보내면 전체]" + ENTER
//                                                    + "- 신청(with 대기) = apply" + ENTER
//                                                    + "- 수락(with 대기) = pending" + ENTER
//                                                    + "- 예정(with 예정) = approve" + ENTER
//                                                    + "- 취소(with 지나감) = cancel" + ENTER
//                                                    + "- 거절(with 지나감) = reject" + ENTER
//                                                    + "- 완료(with 지나감) = complete" + ENTER,
//                                            false
//                                    ),
//                                    query("page", "페이지", "1부터 시작", true)
//                            ),
//                            responseFields(
//                                    body("result[].id", "커피챗 ID(PK)"),
//                                    body("result[].status", "커피챗 상태"),
//                                    body("result[].mentorId", "멘토 ID(PK)"),
//                                    body("result[].name", "멘토 이름"),
//                                    body("result[].profileImageUrl", "멘토 프로필 이미지 URL", "Nullable"),
//                                    body("result[].school", "멘토 학교"),
//                                    body("result[].major", "멘토 전공"),
//                                    body("result[].enteredIn", "멘토 학번"),
//                                    body("hasNext", "다음 스크롤 존재 여부")
//                            )
//                    ))
//            );
//        }
//    }
//}
