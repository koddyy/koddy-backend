package com.koddy.server.common.fixture;

import com.koddy.server.auth.domain.model.AuthMember;
import com.koddy.server.auth.domain.model.AuthToken;
import com.koddy.server.auth.infrastructure.oauth.google.response.GoogleUserResponse;
import com.koddy.server.member.domain.model.Email;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.Nationality;
import com.koddy.server.member.domain.model.mentee.Interest;
import com.koddy.server.member.domain.model.mentee.Mentee;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import static com.koddy.server.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.koddy.server.common.utils.TokenUtils.REFRESH_TOKEN;
import static com.koddy.server.member.domain.model.Nationality.CHINA;
import static com.koddy.server.member.domain.model.Nationality.JAPAN;
import static com.koddy.server.member.domain.model.Nationality.OTHERS;
import static com.koddy.server.member.domain.model.Nationality.USA;
import static com.koddy.server.member.domain.model.Nationality.VIETNAM;

@Getter
@RequiredArgsConstructor
public enum MenteeFixture {
    MENTEE_1(
            Email.from("mentee1@gmail.com"), "멘티1", "s3/Mentee1.png",
            USA, "Hello World~", LanguageFixture.메인_영어_서브_한국어(),
            new Interest("경기대학교", "컴퓨터공학부")
    ),
    MENTEE_2(
            Email.from("mentee2@gmail.com"), "멘티2", "s3/Mentee2.png",
            CHINA, "Hello World~", LanguageFixture.메인_영어_서브_일본어(),
            new Interest("서울대학교", "컴퓨터공학부")
    ),
    MENTEE_3(
            Email.from("mentee3@gmail.com"), "멘티3", "s3/Mentee3.png",
            JAPAN, "Hello World~", LanguageFixture.메인_영어_서브_한국어(),
            new Interest("연세대학교", "컴퓨터공학부")
    ),
    MENTEE_4(
            Email.from("mentee4@gmail.com"), "멘티4", "s3/Mentee4.png",
            VIETNAM, "Hello World~", LanguageFixture.메인_영어_서브_일본어(),
            new Interest("고려대학교", "컴퓨터공학부")
    ),
    MENTEE_5(
            Email.from("mentee5@gmail.com"), "멘티5", "s3/Mentee5.png",
            OTHERS, "Hello World~", LanguageFixture.메인_영어_서브_한국어(),
            new Interest("한양대학교", "컴퓨터공학부")
    ),

    MENTEE_6(
            Email.from("mentee6@gmail.com"), "멘티6", "s3/Mentee6.png",
            USA, "Hello World~", LanguageFixture.메인_영어_서브_일본어(),
            new Interest("경기대학교", "컴퓨터공학부")
    ),
    MENTEE_7(
            Email.from("mentee7@gmail.com"), "멘티7", "s3/Mentee7.png",
            CHINA, "Hello World~", LanguageFixture.메인_영어_서브_한국어(),
            new Interest("서울대학교", "컴퓨터공학부")
    ),
    MENTEE_8(
            Email.from("mentee8@gmail.com"), "멘티8", "s3/Mentee8.png",
            JAPAN, "Hello World~", LanguageFixture.메인_영어_서브_일본어(),
            new Interest("연세대학교", "컴퓨터공학부")
    ),
    MENTEE_9(
            Email.from("mentee9@gmail.com"), "멘티9", "s3/Mentee9.png",
            VIETNAM, "Hello World~", LanguageFixture.메인_영어_서브_한국어(),
            new Interest("고려대학교", "컴퓨터공학부")
    ),
    MENTEE_10(
            Email.from("mentee10@gmail.com"), "멘티10", "s3/Mentee10.png",
            OTHERS, "Hello World~", LanguageFixture.메인_영어_서브_일본어(),
            new Interest("한양대학교", "컴퓨터공학부")
    ),
    ;

    private final Email email;
    private final String name;
    private final String profileImageUrl;
    private final Nationality nationality;
    private final String introduction;
    private final List<Language> languages;
    private final Interest interest;

    public Mentee toDomain() {
        return new Mentee(email, name, profileImageUrl, nationality, introduction, languages, interest);
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
                new AuthMember.MemberInfo(toDomain().apply(1L)),
                new AuthToken(ACCESS_TOKEN, REFRESH_TOKEN)
        );
    }
}
