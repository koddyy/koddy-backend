package com.koddy.server.common.fixture;

import com.koddy.server.acceptance.member.MemberAcceptanceStep;
import com.koddy.server.auth.domain.model.AuthMember;
import com.koddy.server.auth.domain.model.AuthToken;
import com.koddy.server.auth.infrastructure.social.google.response.GoogleUserResponse;
import com.koddy.server.auth.infrastructure.social.kakao.response.KakaoUserResponse;
import com.koddy.server.auth.infrastructure.social.zoom.response.ZoomUserResponse;
import com.koddy.server.member.domain.model.Email;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.SocialPlatform;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.model.mentor.MentoringPeriod;
import com.koddy.server.member.domain.model.mentor.Timeline;
import com.koddy.server.member.domain.model.mentor.UniversityProfile;
import com.koddy.server.member.presentation.request.MentorScheduleRequest;
import com.koddy.server.member.presentation.request.model.MentoringPeriodRequestModel;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.koddy.server.auth.domain.model.AuthToken.ACCESS_TOKEN_HEADER;
import static com.koddy.server.auth.domain.model.AuthToken.REFRESH_TOKEN_HEADER;
import static com.koddy.server.auth.domain.model.oauth.OAuthProvider.GOOGLE;
import static com.koddy.server.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.koddy.server.common.utils.TokenUtils.REFRESH_TOKEN;

public enum MentorFixture {
    MENTOR_1(
            new SocialPlatform(GOOGLE, "ID-MENTOR-1", new Email("mentor1@gmail.com")),
            "멘토1", "s3/Mentor1.png", "Hello World~", LanguageFixture.메인_한국어_서브_영어(),
            new UniversityProfile("경기대학교", "컴퓨터공학부", 18),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.월_수_금()
    ),
    MENTOR_2(
            new SocialPlatform(GOOGLE, "ID-MENTOR-2", new Email("mentor2@gmail.com")),
            "멘토2", "s3/Mentor2.png", "Hello World~", LanguageFixture.메인_한국어_서브_일본어_중국어(),
            new UniversityProfile("서울대학교", "컴퓨터공학부", 19),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.화_목_토()
    ),
    MENTOR_3(
            new SocialPlatform(GOOGLE, "ID-MENTOR-3", new Email("mentor3@gmail.com")),
            "멘토3", "s3/Mentor3.png", "Hello World~", LanguageFixture.메인_한국어_서브_영어(),
            new UniversityProfile("연세대학교", "컴퓨터공학부", 18),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.월_화_수_목_금()
    ),
    MENTOR_4(
            new SocialPlatform(GOOGLE, "ID-MENTOR-4", new Email("mentor4@gmail.com")),
            "멘토4", "s3/Mentor4.png", "Hello World~", LanguageFixture.메인_한국어_서브_일본어_중국어(),
            new UniversityProfile("고려대학교", "컴퓨터공학부", 19),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.주말()
    ),
    MENTOR_5(
            new SocialPlatform(GOOGLE, "ID-MENTOR-5", new Email("mentor5@gmail.com")),
            "멘토5", "s3/Mentor5.png", "Hello World~", LanguageFixture.메인_한국어_서브_영어(),
            new UniversityProfile("한양대학교", "컴퓨터공학부", 18),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.allDays()
    ),
    MENTOR_6(
            new SocialPlatform(GOOGLE, "ID-MENTOR-6", new Email("mentor6@gmail.com")),
            "멘토6", "s3/Mentor6.png", "Hello World~", LanguageFixture.메인_한국어_서브_일본어_중국어(),
            new UniversityProfile("경기대학교", "컴퓨터공학부", 19),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.월_수_금()
    ),
    MENTOR_7(
            new SocialPlatform(GOOGLE, "ID-MENTOR-7", new Email("mentor7@gmail.com")),
            "멘토7", "s3/Mentor7.png", "Hello World~", LanguageFixture.메인_한국어_서브_영어(),
            new UniversityProfile("서울대학교", "컴퓨터공학부", 18),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.화_목_토()
    ),
    MENTOR_8(
            new SocialPlatform(GOOGLE, "ID-MENTOR-8", new Email("mentor8@gmail.com")),
            "멘토8", "s3/Mentor8.png", "Hello World~", LanguageFixture.메인_한국어_서브_일본어_중국어(),
            new UniversityProfile("연세대학교", "컴퓨터공학부", 19),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.월_화_수_목_금()
    ),
    MENTOR_9(
            new SocialPlatform(GOOGLE, "ID-MENTOR-9", new Email("mentor9@gmail.com")),
            "멘토9", "s3/Mentor9.png", "Hello World~", LanguageFixture.메인_한국어_서브_영어(),
            new UniversityProfile("고려대학교", "컴퓨터공학부", 18),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.주말()
    ),
    MENTOR_10(
            new SocialPlatform(GOOGLE, "ID-MENTOR-10", new Email("mentor10@gmail.com")),
            "멘토10", "s3/Mentor10.png", "Hello World~", LanguageFixture.메인_한국어_서브_일본어_중국어(),
            new UniversityProfile("한양대학교", "컴퓨터공학부", 19),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.allDays()
    ),
    MENTOR_11(
            new SocialPlatform(GOOGLE, "ID-MENTOR-11", new Email("mentor11@gmail.com")),
            "멘토11", "s3/Mentor11.png", "Hello World~", LanguageFixture.메인_한국어_서브_영어(),
            new UniversityProfile("경기대학교", "컴퓨터공학부", 18),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.월_수_금()
    ),
    MENTOR_12(
            new SocialPlatform(GOOGLE, "ID-MENTOR-12", new Email("mentor12@gmail.com")),
            "멘토12", "s3/Mentor12.png", "Hello World~", LanguageFixture.메인_한국어_서브_일본어_중국어(),
            new UniversityProfile("서울대학교", "컴퓨터공학부", 19),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.화_목_토()
    ),
    MENTOR_13(
            new SocialPlatform(GOOGLE, "ID-MENTOR-13", new Email("mentor13@gmail.com")),
            "멘토13", "s3/Mentor13.png", "Hello World~", LanguageFixture.메인_한국어_서브_영어(),
            new UniversityProfile("연세대학교", "컴퓨터공학부", 18),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.월_화_수_목_금()
    ),
    MENTOR_14(
            new SocialPlatform(GOOGLE, "ID-MENTOR-14", new Email("mentor14@gmail.com")),
            "멘토14", "s3/Mentor14.png", "Hello World~", LanguageFixture.메인_한국어_서브_일본어_중국어(),
            new UniversityProfile("고려대학교", "컴퓨터공학부", 19),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.주말()
    ),
    MENTOR_15(
            new SocialPlatform(GOOGLE, "ID-MENTOR-15", new Email("mentor15@gmail.com")),
            "멘토15", "s3/Mentor15.png", "Hello World~", LanguageFixture.메인_한국어_서브_영어(),
            new UniversityProfile("한양대학교", "컴퓨터공학부", 18),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.allDays()
    ),
    MENTOR_16(
            new SocialPlatform(GOOGLE, "ID-MENTOR-16", new Email("mentor16@gmail.com")),
            "멘토16", "s3/Mentor16.png", "Hello World~", LanguageFixture.메인_한국어_서브_일본어_중국어(),
            new UniversityProfile("경기대학교", "컴퓨터공학부", 19),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.월_수_금()
    ),
    MENTOR_17(
            new SocialPlatform(GOOGLE, "ID-MENTOR-17", new Email("mentor17@gmail.com")),
            "멘토17", "s3/Mentor17.png", "Hello World~", LanguageFixture.메인_한국어_서브_영어(),
            new UniversityProfile("서울대학교", "컴퓨터공학부", 18),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.화_목_토()
    ),
    MENTOR_18(
            new SocialPlatform(GOOGLE, "ID-MENTOR-18", new Email("mentor18@gmail.com")),
            "멘토18", "s3/Mentor18.png", "Hello World~", LanguageFixture.메인_한국어_서브_일본어_중국어(),
            new UniversityProfile("연세대학교", "컴퓨터공학부", 19),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.월_화_수_목_금()
    ),
    MENTOR_19(
            new SocialPlatform(GOOGLE, "ID-MENTOR-19", new Email("mentor19@gmail.com")),
            "멘토19", "s3/Mentor19.png", "Hello World~", LanguageFixture.메인_한국어_서브_영어(),
            new UniversityProfile("고려대학교", "컴퓨터공학부", 18),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.주말()
    ),
    MENTOR_20(
            new SocialPlatform(GOOGLE, "ID-MENTOR-20", new Email("mentor20@gmail.com")),
            "멘토10", "s3/Mentor20.png", "Hello World~", LanguageFixture.메인_한국어_서브_일본어_중국어(),
            new UniversityProfile("한양대학교", "컴퓨터공학부", 19),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.allDays()
    ),
    ;

    private final SocialPlatform platform;
    private final String name;
    private final String profileImageUrl;
    private final String introduction;
    private final List<Language> languages;
    private final UniversityProfile universityProfile;
    private final MentoringPeriod mentoringPeriod;
    private final List<Timeline> timelines;

    MentorFixture(
            final SocialPlatform platform,
            final String name,
            final String profileImageUrl,
            final String introduction,
            final List<Language> languages,
            final UniversityProfile universityProfile,
            final MentoringPeriod mentoringPeriod,
            final List<Timeline> timelines
    ) {
        this.platform = platform;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.introduction = introduction;
        this.languages = languages;
        this.universityProfile = universityProfile;
        this.mentoringPeriod = mentoringPeriod;
        this.timelines = timelines;
    }

    public Mentor toDomain() {
        final Mentor mentor = new Mentor(platform, name, languages, universityProfile);
        mentor.completeProfile(introduction, profileImageUrl, mentoringPeriod, timelines);
        return mentor;
    }

    public Mentor toDomainWithLanguages(final List<Language> languages) {
        final Mentor mentor = new Mentor(platform, name, languages, universityProfile);
        mentor.completeProfile(introduction, profileImageUrl, mentoringPeriod, timelines);
        return mentor;
    }

    public Mentor toDomainWithMentoringInfo(final MentoringPeriod mentoringPeriod, final List<Timeline> timelines) {
        final Mentor mentor = new Mentor(platform, name, languages, universityProfile);
        mentor.completeProfile(introduction, profileImageUrl, mentoringPeriod, timelines);
        return mentor;
    }

    public Mentor toDomainWithLanguagesAndMentoringInfo(
            final List<Language> languages,
            final MentoringPeriod mentoringPeriod,
            final List<Timeline> timelines
    ) {
        final Mentor mentor = new Mentor(platform, name, languages, universityProfile);
        mentor.completeProfile(introduction, profileImageUrl, mentoringPeriod, timelines);
        return mentor;
    }

    public GoogleUserResponse toGoogleUserResponse() {
        return new GoogleUserResponse(
                platform.getSocialId(),
                this.name,
                this.name,
                this.name,
                this.profileImageUrl,
                this.platform.getEmail().getValue(),
                true,
                "kr"
        );
    }

    public KakaoUserResponse toKakaoUserResponse() {
        return new KakaoUserResponse(
                platform.getSocialId(),
                LocalDateTime.now(),
                null
        );
    }

    public ZoomUserResponse toZoomUserResponse() {
        return new ZoomUserResponse(
                platform.getSocialId(),
                platform.getSocialId(),
                UUID.randomUUID().toString(),
                this.name,
                this.name,
                this.name,
                this.platform.getEmail().getValue(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                "Asia/Seoul",
                UUID.randomUUID().toString()
        );
    }

    public AuthMember toAuthMember() {
        return new AuthMember(
                this.toDomain().apply(1L),
                new AuthToken(ACCESS_TOKEN, REFRESH_TOKEN)
        );
    }

    public AuthMember 회원가입과_로그인을_진행한다() {
        final ExtractableResponse<Response> result = MemberAcceptanceStep.멘토_회원가입_후_로그인을_진행한다(this).extract();
        final long memberId = result.jsonPath().getLong("id");
        final String accessToken = result.header(ACCESS_TOKEN_HEADER).split(" ")[1];
        final String refreshToken = result.cookie(REFRESH_TOKEN_HEADER);

        return new AuthMember(
                memberId,
                this.name,
                new AuthToken(accessToken, refreshToken)
        );
    }

    public AuthMember 회원가입과_로그인을_하고_프로필을_완성시킨다() {
        final ExtractableResponse<Response> result = MemberAcceptanceStep.멘토_회원가입_후_로그인을_진행한다(this).extract();
        final long memberId = result.jsonPath().getLong("id");
        final String accessToken = result.header(ACCESS_TOKEN_HEADER).split(" ")[1];
        final String refreshToken = result.cookie(REFRESH_TOKEN_HEADER);

        MemberAcceptanceStep.멘토_프로필을_완성시킨다(this, accessToken);
        return new AuthMember(
                memberId,
                this.name,
                new AuthToken(accessToken, refreshToken)
        );
    }

    public AuthMember 회원가입과_로그인을_하고_프로필을_완성시킨다(final MentoringPeriodRequestModel period) {
        final ExtractableResponse<Response> result = MemberAcceptanceStep.멘토_회원가입_후_로그인을_진행한다(this).extract();
        final long memberId = result.jsonPath().getLong("id");
        final String accessToken = result.header(ACCESS_TOKEN_HEADER).split(" ")[1];
        final String refreshToken = result.cookie(REFRESH_TOKEN_HEADER);

        MemberAcceptanceStep.멘토_프로필을_완성시킨다(this, period, accessToken);
        return new AuthMember(
                memberId,
                this.name,
                new AuthToken(accessToken, refreshToken)
        );
    }

    public AuthMember 회원가입과_로그인을_하고_프로필을_완성시킨다(final List<MentorScheduleRequest> schedules) {
        final ExtractableResponse<Response> result = MemberAcceptanceStep.멘토_회원가입_후_로그인을_진행한다(this).extract();
        final long memberId = result.jsonPath().getLong("id");
        final String accessToken = result.header(ACCESS_TOKEN_HEADER).split(" ")[1];
        final String refreshToken = result.cookie(REFRESH_TOKEN_HEADER);

        MemberAcceptanceStep.멘토_프로필을_완성시킨다(this, schedules, accessToken);
        return new AuthMember(
                memberId,
                this.name,
                new AuthToken(accessToken, refreshToken)
        );
    }

    public AuthMember 회원가입과_로그인을_하고_프로필을_완성시킨다(
            final MentoringPeriodRequestModel period,
            final List<MentorScheduleRequest> schedules
    ) {
        final ExtractableResponse<Response> result = MemberAcceptanceStep.멘토_회원가입_후_로그인을_진행한다(this).extract();
        final long memberId = result.jsonPath().getLong("id");
        final String accessToken = result.header(ACCESS_TOKEN_HEADER).split(" ")[1];
        final String refreshToken = result.cookie(REFRESH_TOKEN_HEADER);

        MemberAcceptanceStep.멘토_프로필을_완성시킨다(this, period, schedules, accessToken);
        return new AuthMember(
                memberId,
                this.name,
                new AuthToken(accessToken, refreshToken)
        );
    }

    public SocialPlatform getPlatform() {
        return platform;
    }

    public String getName() {
        return name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getIntroduction() {
        return introduction;
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public UniversityProfile getUniversityProfile() {
        return universityProfile;
    }

    public MentoringPeriod getMentoringPeriod() {
        return mentoringPeriod;
    }

    public List<Timeline> getTimelines() {
        return timelines;
    }
}
