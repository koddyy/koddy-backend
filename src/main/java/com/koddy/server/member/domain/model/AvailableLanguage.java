package com.koddy.server.member.domain.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "member_language")
public class AvailableLanguage {
    protected AvailableLanguage() {
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Embedded
    private Language language;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private Member<?> member;

    public AvailableLanguage(final Member<?> member, final Language language) {
        this.member = member;
        this.language = language;
    }

    public Long getId() {
        return id;
    }

    public Language getLanguage() {
        return language;
    }

    public Member<?> getMember() {
        return member;
    }
}
