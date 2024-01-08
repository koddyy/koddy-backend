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
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.koddy.server.member.domain.model.MemberStatus.ACTIVE;
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
public abstract class Member<T extends Member<T>> extends BaseEntity<T> {
    public static final String EMPTY = "EMPTY";

    @Embedded
    protected Email email;

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

    @Enumerated(STRING)
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(20)")
    protected MemberStatus status;

    @OneToMany(mappedBy = "member", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    protected final List<AvailableLanguage> availableLanguages = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    protected final List<Role> roles = new ArrayList<>();

    protected Member(
            final Email email,
            final String name,
            final String profileImageUrl,
            final Nationality nationality,
            final String introduction,
            final List<Language> languages,
            final List<RoleType> roles
    ) {
        this.email = email;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.nationality = nationality;
        this.introduction = introduction;
        this.status = ACTIVE;
        applyLanguages(languages);
        applyRoles(roles);
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

    private void applyRoles(final List<RoleType> roleTypes) {
        this.roles.clear();
        this.roles.addAll(
                roleTypes.stream()
                        .map(it -> new Role(this, it))
                        .toList()
        );
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

    public boolean isActive() {
        return status == ACTIVE;
    }

    public List<Language> getLanguages() {
        return availableLanguages.stream()
                .map(AvailableLanguage::getLanguage)
                .toList();
    }

    public List<String> getAuthorities() {
        return roles.stream()
                .map(Role::getRoleType)
                .map(RoleType::getAuthority)
                .toList();
    }

    protected abstract boolean isProfileComplete();

    public enum MemberType {
        MENTOR, MENTEE
    }
}
