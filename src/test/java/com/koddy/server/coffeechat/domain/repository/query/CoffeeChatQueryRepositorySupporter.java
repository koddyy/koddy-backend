package com.koddy.server.coffeechat.domain.repository.query;

import com.koddy.server.common.RepositoryTest;
import com.koddy.server.common.fixture.MenteeFixture;
import com.koddy.server.common.fixture.MentorFixture;
import com.koddy.server.global.PageCreator;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

public abstract class CoffeeChatQueryRepositorySupporter extends RepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    protected final Mentee[] mentees = new Mentee[20];
    protected final Mentor[] mentors = new Mentor[20];
    protected final Pageable pageable1 = PageCreator.create(1);
    protected final Pageable pageable2 = PageCreator.create(2);

    protected void initMembers() {
        final List<MenteeFixture> menteeFixtures = Arrays.stream(MenteeFixture.values())
                .limit(20)
                .toList();
        Arrays.setAll(mentees, it -> memberRepository.save(menteeFixtures.get(it).toDomain()));

        final List<MentorFixture> mentorFixtures = Arrays.stream(MentorFixture.values())
                .limit(20)
                .toList();
        Arrays.setAll(mentors, it -> memberRepository.save(mentorFixtures.get(it).toDomain()));
    }
}
