package com.koddy.server.common.fixture;

import com.koddy.server.member.domain.model.Email;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.Nationality;
import com.koddy.server.member.domain.model.Password;
import com.koddy.server.member.domain.model.mentee.Interest;
import com.koddy.server.member.domain.model.mentee.Mentee;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.koddy.server.common.utils.EncryptorFactory.getEncryptor;
import static com.koddy.server.member.domain.model.Language.CHINESE;
import static com.koddy.server.member.domain.model.Language.ENGLISH;
import static com.koddy.server.member.domain.model.Language.JAPANESE;
import static com.koddy.server.member.domain.model.Language.KOREAN;
import static com.koddy.server.member.domain.model.Language.VIETNAMESE;
import static com.koddy.server.member.domain.model.Nationality.CHINA;
import static com.koddy.server.member.domain.model.Nationality.JAPAN;
import static com.koddy.server.member.domain.model.Nationality.OTHERS;
import static com.koddy.server.member.domain.model.Nationality.USA;
import static com.koddy.server.member.domain.model.Nationality.VIETNAM;

@Getter
@RequiredArgsConstructor
public enum MenteeFixture {
    MENTEE_1(
            Email.init("mentee1@gmail.com"),
            Password.encrypt("Koddy123!@#", getEncryptor()),
            "멘티1", USA, "s3/Mentee1.png", "Hello World~",
            List.of(KOREAN, ENGLISH),
            new Interest("경기대학교", "컴퓨터공학부")
    ),
    MENTEE_2(
            Email.init("mentee2@gmail.com"),
            Password.encrypt("Koddy123!@#", getEncryptor()),
            "멘티2", CHINA, "s3/Mentee2.png", "Hello World~",
            List.of(KOREAN, ENGLISH, CHINESE),
            new Interest("서울대학교", "컴퓨터공학부")
    ),
    MENTEE_3(
            Email.init("mentee3@gmail.com"),
            Password.encrypt("Koddy123!@#", getEncryptor()),
            "멘티3", JAPAN, "s3/Mentee3.png", "Hello World~",
            List.of(KOREAN, ENGLISH, JAPANESE),
            new Interest("연세대학교", "컴퓨터공학부")
    ),
    MENTEE_4(
            Email.init("mentee4@gmail.com"),
            Password.encrypt("Koddy123!@#", getEncryptor()),
            "멘티4", VIETNAM, "s3/Mentee4.png", "Hello World~",
            List.of(KOREAN, ENGLISH, CHINESE, VIETNAMESE),
            new Interest("고려대학교", "컴퓨터공학부")
    ),
    MENTEE_5(
            Email.init("mentee5@gmail.com"),
            Password.encrypt("Koddy123!@#", getEncryptor()),
            "멘티5", OTHERS, "s3/Mentee5.png", "Hello World~",
            List.of(KOREAN, ENGLISH, CHINESE, JAPANESE, VIETNAMESE),
            new Interest("한양대학교", "컴퓨터공학부")
    ),

    MENTEE_6(
            Email.init("mentee6@gmail.com"),
            Password.encrypt("Koddy123!@#", getEncryptor()),
            "멘티6", USA, "s3/Mentee6.png", "Hello World~",
            List.of(KOREAN, ENGLISH),
            new Interest("경기대학교", "컴퓨터공학부")
    ),
    MENTEE_7(
            Email.init("mentee7@gmail.com"),
            Password.encrypt("Koddy123!@#", getEncryptor()),
            "멘티7", CHINA, "s3/Mentee7.png", "Hello World~",
            List.of(KOREAN, ENGLISH, CHINESE),
            new Interest("서울대학교", "컴퓨터공학부")
    ),
    MENTEE_8(
            Email.init("mentee8@gmail.com"),
            Password.encrypt("Koddy123!@#", getEncryptor()),
            "멘티8", JAPAN, "s3/Mentee8.png", "Hello World~",
            List.of(KOREAN, ENGLISH, JAPANESE),
            new Interest("연세대학교", "컴퓨터공학부")
    ),
    MENTEE_9(
            Email.init("mentee9@gmail.com"),
            Password.encrypt("Koddy123!@#", getEncryptor()),
            "멘티9", VIETNAM, "s3/Mentee9.png", "Hello World~",
            List.of(KOREAN, ENGLISH, CHINESE, VIETNAMESE),
            new Interest("고려대학교", "컴퓨터공학부")
    ),
    MENTEE_10(
            Email.init("mentee10@gmail.com"),
            Password.encrypt("Koddy123!@#", getEncryptor()),
            "멘티10", OTHERS, "s3/Mentee10.png", "Hello World~",
            List.of(KOREAN, ENGLISH, CHINESE, JAPANESE, VIETNAMESE),
            new Interest("한양대학교", "컴퓨터공학부")
    ),
    ;

    private final Email email;
    private final Password password;
    private final String name;
    private final Nationality nationality;
    private final String profileImageUrl;
    private final String introduction;
    private final List<Language> languages;
    private final Interest interest;

    public Mentee toDomain() {
        final Mentee mentee = new Mentee(email, password);
        mentee.complete(name, nationality, profileImageUrl, introduction, languages, interest);
        return mentee;
    }
}
