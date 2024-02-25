package com.koddy.server.acceptance.member;

import com.koddy.server.common.AcceptanceTest;
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static com.koddy.server.acceptance.member.MemberAcceptanceStep.멘토_마이페이지_프로필을_조회한다;
import static com.koddy.server.acceptance.member.MemberAcceptanceStep.멘티_마이페이지_프로필을_조회한다;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.member.domain.model.Language.Category.EN;
import static com.koddy.server.member.domain.model.Language.Category.KR;
import static com.koddy.server.member.domain.model.Nationality.KOREA;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(DatabaseCleanerEachCallbackExtension.class)
@DisplayName("[Acceptance Test] 사용자 마이페이지(Private) 프로필 조회")
public class MemberPrivateProfileQueryAcceptanceTest extends AcceptanceTest {
    @Nested
    @DisplayName("멘토 마이페이지 프로필 조회 API")
    class GetMentorProfile {
        @Test
        @DisplayName("멘토 마이페이지 프로필을 조회한다 (미완성 프로필)")
        void successWithUncomplete() {
            final String accessToken = MENTOR_1.회원가입과_로그인을_진행한다().token().accessToken();
            멘토_마이페이지_프로필을_조회한다(accessToken)
                    .statusCode(OK.value())
                    .body("id", notNullValue(Long.class))
                    .body("email", is(MENTOR_1.getPlatform().getEmail().getValue()))
                    .body("name", is(MENTOR_1.getName()))
                    .body("profileImageUrl", nullValue())
                    .body("nationality", is(KOREA.code))
                    .body("introduction", nullValue())
                    .body("languages.main", is(KR.getCode()))
                    .body("languages.sub", containsInAnyOrder(List.of(EN.getCode()).toArray()))
                    .body("school", is(MENTOR_1.getUniversityProfile().getSchool()))
                    .body("major", is(MENTOR_1.getUniversityProfile().getMajor()))
                    .body("enteredIn", is(MENTOR_1.getUniversityProfile().getEnteredIn()))
                    .body("authenticated", is(false))
                    .body("period", nullValue())
                    .body("schedules", hasSize(0))
                    .body("role", is("mentor"))
                    .body("profileComplete", is(false));
        }

        @Test
        @DisplayName("멘토 마이페이지 프로필을 조회한다 (완성 프로필)")
        void successWithComplete() {
            final String accessToken = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다().token().accessToken();
            멘토_마이페이지_프로필을_조회한다(accessToken)
                    .statusCode(OK.value())
                    .body("id", notNullValue(Long.class))
                    .body("email", is(MENTOR_1.getPlatform().getEmail().getValue()))
                    .body("name", is(MENTOR_1.getName()))
                    .body("profileImageUrl", is(MENTOR_1.getProfileImageUrl()))
                    .body("nationality", is(KOREA.code))
                    .body("introduction", is(MENTOR_1.getIntroduction()))
                    .body("languages.main", is(KR.getCode()))
                    .body("languages.sub", containsInAnyOrder(List.of(EN.getCode()).toArray()))
                    .body("school", is(MENTOR_1.getUniversityProfile().getSchool()))
                    .body("major", is(MENTOR_1.getUniversityProfile().getMajor()))
                    .body("enteredIn", is(MENTOR_1.getUniversityProfile().getEnteredIn()))
                    .body("authenticated", is(false))
                    .body("period.startDate", is(MENTOR_1.getMentoringPeriod().getStartDate().toString()))
                    .body("period.endDate", is(MENTOR_1.getMentoringPeriod().getEndDate().toString()))
                    .body("schedules.dayOfWeek", contains(
                            MENTOR_1.getTimelines()
                                    .stream()
                                    .map(it -> it.getDayOfWeek().getKor())
                                    .toList()
                                    .toArray()
                    ))
                    .body("schedules.start.hour", contains(
                            MENTOR_1.getTimelines()
                                    .stream()
                                    .map(it -> it.getStartTime().getHour())
                                    .toList()
                                    .toArray()
                    ))
                    .body("schedules.start.minute", contains(
                            MENTOR_1.getTimelines()
                                    .stream()
                                    .map(it -> it.getStartTime().getMinute())
                                    .toList()
                                    .toArray()
                    ))
                    .body("schedules.end.hour", contains(
                            MENTOR_1.getTimelines()
                                    .stream()
                                    .map(it -> it.getEndTime().getHour())
                                    .toList()
                                    .toArray()
                    ))
                    .body("schedules.end.minute", contains(
                            MENTOR_1.getTimelines()
                                    .stream()
                                    .map(it -> it.getEndTime().getMinute())
                                    .toList()
                                    .toArray()
                    ))
                    .body("role", is("mentor"))
                    .body("profileComplete", is(true));
        }
    }

    @Nested
    @DisplayName("멘티 마이페이지 프로필 조회 API")
    class GetMenteeProfile {
        @Test
        @DisplayName("멘티 마이페이지 프로필을 조회한다 (미완성 프로필)")
        void successWithUncomplete() {
            final String accessToken = MENTEE_1.회원가입과_로그인을_진행한다().token().accessToken();
            멘티_마이페이지_프로필을_조회한다(accessToken)
                    .statusCode(OK.value())
                    .body("id", notNullValue(Long.class))
                    .body("email", is(MENTEE_1.getPlatform().getEmail().getValue()))
                    .body("name", is(MENTEE_1.getName()))
                    .body("profileImageUrl", nullValue())
                    .body("nationality", is(MENTEE_1.getNationality().code))
                    .body("introduction", nullValue())
                    .body("languages.main", is(EN.getCode()))
                    .body("languages.sub", containsInAnyOrder(List.of(KR.getCode()).toArray()))
                    .body("interestSchool", is(MENTEE_1.getInterest().getSchool()))
                    .body("interestMajor", is(MENTEE_1.getInterest().getMajor()))
                    .body("role", is("mentee"))
                    .body("profileComplete", is(false));
        }

        @Test
        @DisplayName("멘티 마이페이지 프로필을 조회한다 (완성 프로필)")
        void successWithComplete() {
            final String accessToken = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다().token().accessToken();
            멘티_마이페이지_프로필을_조회한다(accessToken)
                    .statusCode(OK.value())
                    .body("id", notNullValue(Long.class))
                    .body("email", is(MENTEE_1.getPlatform().getEmail().getValue()))
                    .body("name", is(MENTEE_1.getName()))
                    .body("profileImageUrl", is(MENTEE_1.getProfileImageUrl()))
                    .body("nationality", is(MENTEE_1.getNationality().code))
                    .body("introduction", is(MENTEE_1.getIntroduction()))
                    .body("languages.main", is(EN.getCode()))
                    .body("languages.sub", containsInAnyOrder(List.of(KR.getCode()).toArray()))
                    .body("interestSchool", is(MENTEE_1.getInterest().getSchool()))
                    .body("interestMajor", is(MENTEE_1.getInterest().getMajor()))
                    .body("role", is("mentee"))
                    .body("profileComplete", is(true));
        }
    }
}
