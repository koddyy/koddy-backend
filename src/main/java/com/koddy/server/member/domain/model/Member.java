package com.koddy.server.member.domain.model;

import com.koddy.server.global.base.BaseEntity;
import com.koddy.server.global.encrypt.Encryptor;
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
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.koddy.server.member.domain.model.Nationality.ANONYMOUS;
import static com.koddy.server.member.exception.MemberExceptionCode.AVAILABLE_LANGUAGE_MUST_BE_EXISTS;
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

    @Embedded
    protected Password password;

    @Column(name = "name", nullable = false)
    protected String name;

    @Enumerated(STRING)
    @Column(name = "nationality", nullable = false, columnDefinition = "VARCHAR(50)")
    protected Nationality nationality;

    @Column(name = "profile_image_url", nullable = false)
    protected String profileImageUrl;

    @Lob
    @Column(name = "introduction", nullable = false, columnDefinition = "TEXT")
    protected String introduction;

    @OneToMany(mappedBy = "member", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    protected final List<AvailableLanguage> availableLanguages = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    protected final List<Role> roles = new ArrayList<>();

    protected Member(final Email email, final Password password, final List<RoleType> roleTypes) {
        this.email = email;
        this.password = password;
        this.name = EMPTY;
        this.nationality = ANONYMOUS;
        this.profileImageUrl = EMPTY;
        this.introduction = EMPTY;
        applyRoles(roleTypes);
    }

    private void applyRoles(final List<RoleType> roleTypes) {
        this.roles.clear();
        this.roles.addAll(
                roleTypes.stream()
                        .map(it -> new Role(this, it))
                        .toList()
        );
    }

    protected void complete(
            final String name,
            final Nationality nationality,
            final String profileImageUrl,
            final String introduction,
            final List<Language> languages
    ) {
        this.name = name;
        this.nationality = nationality;
        this.profileImageUrl = checkEmptyValue(profileImageUrl);
        this.introduction = checkEmptyValue(introduction);
        applyLanguages(languages);
    }

    private static String checkEmptyValue(final String value) {
        if (StringUtils.hasText(value)) {
            return value;
        }
        return EMPTY;
    }

    private void applyLanguages(final List<Language> languages) {
        if (languages.isEmpty()) {
            throw new MemberException(AVAILABLE_LANGUAGE_MUST_BE_EXISTS);
        }

        this.availableLanguages.clear();
        this.availableLanguages.addAll(
                languages.stream()
                        .map(it -> new AvailableLanguage(this, it))
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
        this.profileImageUrl = checkEmptyValue(profileImageUrl);
        this.introduction = checkEmptyValue(introduction);
        applyLanguages(languages);
    }

    protected void updatePassword(final String currentPassword, final String updatePassword, final Encryptor encryptor) {
        this.password = this.password.update(currentPassword, updatePassword, encryptor);
    }

    public boolean isAuthenticated() {
        return email.isAuthenticated();
    }

    public void authenticate() {
        email.activate();
    }

    public List<Language> getLanguages() {
        return availableLanguages.stream()
                .map(AvailableLanguage::getLanguage)
                .toList();
    }

    public List<RoleType> getRoleTypes() {
        return roles.stream()
                .map(Role::getRoleType)
                .toList();
    }
}
