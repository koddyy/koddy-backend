package com.koddy.server.auth.domain.model;

import com.koddy.server.global.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "member_token")
public class Token extends BaseEntity<Token> {
    protected Token() {
    }

    @Column(name = "member_id", nullable = false, unique = true)
    private Long memberId;

    @Column(name = "refresh_token", nullable = false, unique = true)
    private String refreshToken;

    public Token(final Long memberId, final String refreshToken) {
        this.memberId = memberId;
        this.refreshToken = refreshToken;
    }

    public void updateRefreshToken(final String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
