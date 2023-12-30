package com.koddy.server.member.domain.model.mentee;

import com.koddy.server.global.encrypt.Encryptor;
import com.koddy.server.member.domain.model.Email;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.Member;
import com.koddy.server.member.domain.model.Nationality;
import com.koddy.server.member.domain.model.Password;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.koddy.server.member.domain.model.RoleType.MENTEE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "mentee")
public class Mentee extends Member<Mentee> {
    @Embedded
    private Interest interest;

    public Mentee(final Email email, final Password password) {
        super(email, password, List.of(MENTEE));
        this.interest = new Interest(EMPTY, EMPTY);
    }

    public void complete(
            final String name,
            final Nationality nationality,
            final String profileImageUrl,
            final String introduction,
            final List<Language> languages,
            final Interest interest
    ) {
        super.complete(name, nationality, profileImageUrl, introduction, languages);
        this.interest = interest;
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
    }

    public void updatePassword(final String currentPassword, final String updatePassword, final Encryptor encryptor) {
        super.updatePassword(currentPassword, updatePassword, encryptor);
    }
}
