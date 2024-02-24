package com.koddy.server.member.domain.model.mentee;

import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.Member;
import com.koddy.server.member.domain.model.Nationality;
import com.koddy.server.member.domain.model.Role;
import com.koddy.server.member.domain.model.SocialPlatform;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.koddy.server.member.domain.model.Role.MENTEE;

@Entity
@Table(name = "mentee")
@DiscriminatorValue(value = Role.MENTEE_VALUE)
public class Mentee extends Member<Mentee> {
    protected Mentee() {
        super();
    }

    @Embedded
    private Interest interest;

    public Mentee(
            final SocialPlatform platform,
            final String name,
            final Nationality nationality,
            final List<Language> languages,
            final Interest interest
    ) {
        super(
                platform,
                name,
                nationality,
                MENTEE,
                languages
        );
        this.interest = interest;
    }

    public void completeInfo(
            final String introduction,
            final String profileImageUrl
    ) {
        super.completeInfo(introduction, profileImageUrl);
        checkProfileCompleted();
    }

    public void updateBasicInfo(
            final String name,
            final Nationality nationality,
            final String profileImageUrl,
            final String introduction,
            final List<Language> languages,
            final String interestSchool,
            final String interestMajor
    ) {
        super.updateBasicInfo(name, nationality, profileImageUrl, introduction, languages);
        this.interest = this.interest.update(interestSchool, interestMajor);
        checkProfileCompleted();
    }

    @Override
    public void checkProfileCompleted() {
        super.profileComplete = isCompleted();
    }

    private boolean isCompleted() {
        return StringUtils.hasText(introduction)
                && StringUtils.hasText(profileImageUrl);
    }

    public Interest getInterest() {
        return interest;
    }
}
