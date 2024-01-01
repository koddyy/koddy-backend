package com.koddy.server.common.fixture;

import com.koddy.server.auth.domain.model.AuthMember;
import com.koddy.server.auth.domain.model.AuthToken;
import com.koddy.server.auth.infrastructure.oauth.google.response.GoogleUserResponse;
import com.koddy.server.member.domain.model.Email;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.model.mentor.Schedule;
import com.koddy.server.member.domain.model.mentor.UniversityProfile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.koddy.server.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.koddy.server.common.utils.TokenUtils.REFRESH_TOKEN;
import static com.koddy.server.member.domain.model.Language.CHINESE;
import static com.koddy.server.member.domain.model.Language.ENGLISH;
import static com.koddy.server.member.domain.model.Language.JAPANESE;
import static com.koddy.server.member.domain.model.Language.KOREAN;
import static com.koddy.server.member.domain.model.Language.VIETNAMESE;

@Getter
@RequiredArgsConstructor
public enum MentorFixture {
    MENTOR_1(
            Email.from("mentor1@gmail.com"), "멘토1", "s3/Mentor1.png",
            "Hello World~", List.of(KOREAN, ENGLISH),
            new UniversityProfile("경기대학교", "컴퓨터공학부", 3),
            "mentor1-url", ScheduleFixture.월_수_금()
    ),
    MENTOR_2(
            Email.from("mentor2@gmail.com"), "멘토2", "s3/Mentor2.png",
            "Hello World~", List.of(KOREAN, ENGLISH, CHINESE),
            new UniversityProfile("서울대학교", "컴퓨터공학부", 4),
            "mentor2-url", ScheduleFixture.화_목_토()
    ),
    MENTOR_3(
            Email.from("mentor3@gmail.com"), "멘토3", "s3/Mentor3.png",
            "Hello World~", List.of(KOREAN, ENGLISH, JAPANESE),
            new UniversityProfile("연세대학교", "컴퓨터공학부", 3),
            "mentor3-url", ScheduleFixture.월_화_수_목_금()
    ),
    MENTOR_4(
            Email.from("mentor4@gmail.com"), "멘토4", "s3/Mentor4.png",
            "Hello World~", List.of(KOREAN, ENGLISH, CHINESE, VIETNAMESE),
            new UniversityProfile("고려대학교", "컴퓨터공학부", 4),
            "mentor4-url", ScheduleFixture.주말()
    ),
    MENTOR_5(
            Email.from("mentor5@gmail.com"), "멘토5", "s3/Mentor5.png",
            "Hello World~", List.of(KOREAN, ENGLISH, CHINESE, JAPANESE, VIETNAMESE),
            new UniversityProfile("한양대학교", "컴퓨터공학부", 3),
            "mentor5-url", ScheduleFixture.allDays()
    ),

    MENTOR_6(
            Email.from("mentor6@gmail.com"), "멘토6", "s3/Mentor6.png",
            "Hello World~", List.of(KOREAN, ENGLISH),
            new UniversityProfile("경기대학교", "컴퓨터공학부", 4),
            "mentor6-url", ScheduleFixture.월_수_금()
    ),
    MENTOR_7(
            Email.from("mentor7@gmail.com"), "멘토7", "s3/Mentor7.png",
            "Hello World~", List.of(KOREAN, ENGLISH, CHINESE),
            new UniversityProfile("서울대학교", "컴퓨터공학부", 3),
            "mentor7-url", ScheduleFixture.화_목_토()
    ),
    MENTOR_8(
            Email.from("mentor8@gmail.com"), "멘토8", "s3/Mentor8.png",
            "Hello World~", List.of(KOREAN, ENGLISH, JAPANESE),
            new UniversityProfile("연세대학교", "컴퓨터공학부", 4),
            "mentor8-url", ScheduleFixture.월_화_수_목_금()
    ),
    MENTOR_9(
            Email.from("mentor9@gmail.com"), "멘토9", "s3/Mentor9.png",
            "Hello World~", List.of(KOREAN, ENGLISH, CHINESE, VIETNAMESE),
            new UniversityProfile("고려대학교", "컴퓨터공학부", 3),
            "mentor9-url", ScheduleFixture.주말()
    ),
    MENTOR_10(
            Email.from("mentor10@gmail.com"), "멘토10", "s3/Mentor10.png",
            "Hello World~", List.of(KOREAN, ENGLISH, CHINESE, JAPANESE, VIETNAMESE),
            new UniversityProfile("한양대학교", "컴퓨터공학부", 4),
            "mentor10-url", ScheduleFixture.allDays()
    ),
    ;

    private final Email email;
    private final String name;
    private final String profileImageUrl;
    private final String introduction;
    private final List<Language> languages;
    private final UniversityProfile universityProfile;
    private final String meetingUrl;
    private final List<Schedule> schedules;

    public Mentor toDomain() {
        return new Mentor(email, name, profileImageUrl, introduction, languages, universityProfile, meetingUrl, schedules);
    }

    public Mentor toDomain(final List<Schedule> schedules) {
        return new Mentor(email, name, profileImageUrl, introduction, languages, universityProfile, meetingUrl, schedules);
    }

    public GoogleUserResponse toGoogleUserResponse() {
        return new GoogleUserResponse(
                UUID.randomUUID().toString(),
                this.name,
                this.name,
                this.name,
                this.profileImageUrl,
                this.email.getValue(),
                true,
                "kr"
        );
    }

    public AuthMember toAuthMember() {
        return new AuthMember(
                new AuthMember.MemberInfo(toDomain().apply(1L, LocalDateTime.now())),
                new AuthToken(ACCESS_TOKEN, REFRESH_TOKEN)
        );
    }
}
