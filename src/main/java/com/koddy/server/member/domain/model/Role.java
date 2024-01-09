package com.koddy.server.member.domain.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "member_role")
public class Role {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Enumerated(STRING)
    @Column(name = "role_type", nullable = false, columnDefinition = "VARCHAR(30)")
    private Type type;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private Member<?> member;

    public Role(final Member<?> member, final Type type) {
        this.member = member;
        this.type = type;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Type {
        MENTOR("ROLE_MENTOR", "멘토"),
        MENTEE("ROLE_MENTEE", "멘티"),
        ;

        private final String authority;
        private final String value;
    }
}
