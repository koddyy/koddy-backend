package com.koddy.server.member.domain.model;

import com.koddy.server.global.base.BaseEntity;
import com.koddy.server.member.exception.MemberException;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.koddy.server.member.domain.model.Member.Status.ACTIVE;
import static com.koddy.server.member.exception.MemberExceptionCode.AVAILABLE_LANGUAGE_MUST_EXISTS;
import static com.koddy.server.member.exception.MemberExceptionCode.MAIN_LANGUAGE_MUST_BE_ONLY_ONE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.InheritanceType.JOINED;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Inheritance(strategy = JOINED)
@DiscriminatorColumn(name = "type")
@Entity
@Table(name = "member")
@SQLRestriction("status = 'ACTIVE'")
public abstract class Member<T extends Member<T>> extends BaseEntity<T> {
    @Embedded
    protected SocialPlatform platform;

    @Column(name = "name", nullable = false)
    protected String name;

    @Column(name = "profile_image_url", nullable = false)
    protected String profileImageUrl;

    @Enumerated(STRING)
    @Column(name = "nationality", nullable = false, columnDefinition = "VARCHAR(50)")
    protected Nationality nationality;

    @Lob
    @Column(name = "introduction", columnDefinition = "TEXT")
    protected String introduction;

    @Column(name = "profile_complete", nullable = false, columnDefinition = "TINYINT")
    protected boolean profileComplete;

    @Enumerated(STRING)
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(20)")
    protected Status status;

    @Enumerated(STRING)
    @Column(name = "role", nullable = false, columnDefinition = "VARCHAR(30)")
    protected Role role;

    @OneToMany(mappedBy = "member", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    protected final List<AvailableLanguage> availableLanguages = new ArrayList<>();

    protected Member(
            final SocialPlatform platform,
            final String name,
            final String profileImageUrl,
            final Nationality nationality,
            final Role role,
            final List<Language> languages
    ) {
        this.platform = platform;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.nationality = nationality;
        this.role = role;
        this.profileComplete = false;
        this.status = ACTIVE;
        applyLanguages(languages);
    }

    private void applyLanguages(final List<Language> languages) {
        validateEmpty(languages);
        validateMainLanguageIsOnlyOne(languages);

        this.availableLanguages.clear();
        this.availableLanguages.addAll(
                languages.stream()
                        .map(it -> new AvailableLanguage(this, it))
                        .toList()
        );
    }

    private void validateEmpty(final List<Language> languages) {
        if (CollectionUtils.isEmpty(languages)) {
            throw new MemberException(AVAILABLE_LANGUAGE_MUST_EXISTS);
        }
    }

    private void validateMainLanguageIsOnlyOne(final List<Language> languages) {
        final long mainLanguageCount = languages.stream()
                .filter(it -> it.getType() == Language.Type.MAIN)
                .count();

        if (mainLanguageCount != 1) {
            throw new MemberException(MAIN_LANGUAGE_MUST_BE_ONLY_ONE);
        }
    }

    protected void completeInfo(final String introduction) {
        this.introduction = introduction;
    }

    protected void checkProfileCompleted() {
        profileComplete = isProfileComplete();
    }

    protected void updateBasicInfo(
            final String name,
            final Nationality nationality,
            final String profileImageUrl,
            final String introduction,
            final List<Language> languages
    ) {
        this.name = name;
        this.nationality = nationality;
        this.profileImageUrl = profileImageUrl;
        this.introduction = introduction;
        applyLanguages(languages);
    }

    public void syncEmail(final Email email) {
        this.platform = this.platform.syncEmail(email);
    }

    public List<Language> getLanguages() {
        return availableLanguages.stream()
                .map(AvailableLanguage::getLanguage)
                .toList();
    }

    public String getAuthority() {
        return role.getAuthority();
    }

    public boolean profileComplete() {
        return profileComplete;
    }

    public abstract boolean isProfileComplete();

    public enum Status {
        ACTIVE, INACTIVE, BAN
    }
}
