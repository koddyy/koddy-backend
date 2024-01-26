package com.koddy.server.member.domain.model.mentee;

import com.koddy.server.member.domain.model.Email;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.Member;
import com.koddy.server.member.domain.model.Nationality;
import com.koddy.server.member.domain.model.Role;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.koddy.server.member.domain.model.Role.MENTEE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "mentee")
@DiscriminatorValue(value = Role.Value.MENTEE)
public class Mentee extends Member<Mentee> {
    @Embedded
    private Interest interest;

    public Mentee(
            final Email email,
            final String name,
            final String profileImageUrl,
            final Nationality nationality,
            final List<Language> languages,
            final Interest interest
    ) {
        super(email, name, profileImageUrl, nationality, MENTEE, languages);
        this.interest = interest;
    }

    public void completeInfo(final String introduction) {
        super.completeInfo(introduction);
        super.checkProfileCompleted();
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
    public boolean isProfileComplete() {
        return StringUtils.hasText(introduction);
    }
}
