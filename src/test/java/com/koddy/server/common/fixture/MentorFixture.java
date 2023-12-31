package com.koddy.server.common.fixture;

import com.koddy.server.member.domain.model.Email;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.Nationality;
import com.koddy.server.member.domain.model.Password;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.model.mentor.Schedule;
import com.koddy.server.member.domain.model.mentor.UniversityProfile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.koddy.server.common.utils.EncryptorFactory.getEncryptor;
import static com.koddy.server.member.domain.model.Language.CHINESE;
import static com.koddy.server.member.domain.model.Language.ENGLISH;
import static com.koddy.server.member.domain.model.Language.JAPANESE;
import static com.koddy.server.member.domain.model.Language.KOREAN;
import static com.koddy.server.member.domain.model.Language.VIETNAMESE;
import static com.koddy.server.member.domain.model.Nationality.KOREA;

@Getter
@RequiredArgsConstructor
public enum MentorFixture {
    MENTOR_1(
            Email.init("mentor1@gmail.com"),
            Password.encrypt("Koddy123!@#", getEncryptor()),
            "멘토1", KOREA, "s3/Mentor1.png", "Hello World~",
            List.of(KOREAN, ENGLISH),
            new UniversityProfile("경기대학교", "컴퓨터공학부", 3), "mentor1-url",
            ScheduleFixture.월_수_금()
    ),
    MENTOR_2(
            Email.init("mentor2@gmail.com"),
            Password.encrypt("Koddy123!@#", getEncryptor()),
            "멘토2", KOREA, "s3/Mentor2.png", "Hello World~",
            List.of(KOREAN, ENGLISH, CHINESE),
            new UniversityProfile("서울대학교", "컴퓨터공학부", 4), "mentor2-url",
            ScheduleFixture.화_목_토()
    ),
    MENTOR_3(
            Email.init("mentor3@gmail.com"),
            Password.encrypt("Koddy123!@#", getEncryptor()),
            "멘토3", KOREA, "s3/Mentor3.png", "Hello World~",
            List.of(KOREAN, ENGLISH, JAPANESE),
            new UniversityProfile("연세대학교", "컴퓨터공학부", 3), "mentor3-url",
            ScheduleFixture.월_화_수_목_금()
    ),
    MENTOR_4(
            Email.init("mentor4@gmail.com"),
            Password.encrypt("Koddy123!@#", getEncryptor()),
            "멘토4", KOREA, "s3/Mentor4.png", "Hello World~",
            List.of(KOREAN, ENGLISH, CHINESE, VIETNAMESE),
            new UniversityProfile("고려대학교", "컴퓨터공학부", 4), "mentor4-url",
            ScheduleFixture.주말()
    ),
    MENTOR_5(
            Email.init("mentor5@gmail.com"),
            Password.encrypt("Koddy123!@#", getEncryptor()),
            "멘토5", KOREA, "s3/Mentor5.png", "Hello World~",
            List.of(KOREAN, ENGLISH, CHINESE, JAPANESE, VIETNAMESE),
            new UniversityProfile("한양대학교", "컴퓨터공학부", 3), "mentor5-url",
            ScheduleFixture.allDays()
    ),

    MENTOR_6(
            Email.init("mentor6@gmail.com"),
            Password.encrypt("Koddy123!@#", getEncryptor()),
            "멘토6", KOREA, "s3/Mentor6.png", "Hello World~",
            List.of(KOREAN, ENGLISH),
            new UniversityProfile("경기대학교", "컴퓨터공학부", 4), "mentor6-url",
            ScheduleFixture.월_수_금()
    ),
    MENTOR_7(
            Email.init("mentor7@gmail.com"),
            Password.encrypt("Koddy123!@#", getEncryptor()),
            "멘토7", KOREA, "s3/Mentor7.png", "Hello World~",
            List.of(KOREAN, ENGLISH, CHINESE),
            new UniversityProfile("서울대학교", "컴퓨터공학부", 3), "mentor7-url",
            ScheduleFixture.화_목_토()
    ),
    MENTOR_8(
            Email.init("mentor8@gmail.com"),
            Password.encrypt("Koddy123!@#", getEncryptor()),
            "멘토8", KOREA, "s3/Mentor8.png", "Hello World~",
            List.of(KOREAN, ENGLISH, JAPANESE),
            new UniversityProfile("연세대학교", "컴퓨터공학부", 4), "mentor8-url",
            ScheduleFixture.월_화_수_목_금()
    ),
    MENTOR_9(
            Email.init("mentor9@gmail.com"),
            Password.encrypt("Koddy123!@#", getEncryptor()),
            "멘토9", KOREA, "s3/Mentor9.png", "Hello World~",
            List.of(KOREAN, ENGLISH, CHINESE, VIETNAMESE),
            new UniversityProfile("고려대학교", "컴퓨터공학부", 3), "mentor9-url",
            ScheduleFixture.주말()
    ),
    MENTOR_10(
            Email.init("mentor10@gmail.com"),
            Password.encrypt("Koddy123!@#", getEncryptor()),
            "멘토10", KOREA, "s3/Mentor10.png", "Hello World~",
            List.of(KOREAN, ENGLISH, CHINESE, JAPANESE, VIETNAMESE),
            new UniversityProfile("한양대학교", "컴퓨터공학부", 4), "mentor10-url",
            ScheduleFixture.allDays()
    ),
    ;

    private final Email email;
    private final Password password;
    private final String name;
    private final Nationality nationality;
    private final String profileImageUrl;
    private final String introduction;
    private final List<Language> languages;
    private final UniversityProfile universityProfile;
    private final String meetingUrl;
    private final List<Schedule> schedules;

    public Mentor toDomain() {
        final Mentor mentor = new Mentor(email, password);
        mentor.complete(name, nationality, profileImageUrl, introduction, languages, universityProfile, meetingUrl, schedules);
        mentor.authenticate();
        return mentor;
    }

    public Mentor toDomain(final List<Schedule> schedules) {
        final Mentor mentor = new Mentor(email, password);
        mentor.complete(name, nationality, profileImageUrl, introduction, languages, universityProfile, meetingUrl, schedules);
        mentor.authenticate();
        return mentor;
    }
}
