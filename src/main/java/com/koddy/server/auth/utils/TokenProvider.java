package com.koddy.server.auth.utils;

import com.koddy.server.auth.exception.AuthException;
import com.koddy.server.member.domain.model.RoleType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_TOKEN;

@Slf4j
@Component
public class TokenProvider {
    private final SecretKey secretKey;
    private final long accessTokenValidityInSeconds;
    private final long refreshTokenValidityInSeconds;

    public TokenProvider(
            @Value("${jwt.secret-key}") final String secretKey,
            @Value("${jwt.access-token-validity-seconds}") final long accessTokenValidityInSeconds,
            @Value("${jwt.refresh-token-validity-seconds}") final long refreshTokenValidityInSeconds
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidityInSeconds = accessTokenValidityInSeconds;
        this.refreshTokenValidityInSeconds = refreshTokenValidityInSeconds;
    }

    public String createAccessToken(final Long memberId, final List<RoleType> roles) {
        final ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        final ZonedDateTime tokenValidity = now.plusSeconds(accessTokenValidityInSeconds);

        return Jwts.builder()
                .claim("id", memberId)
                .claim("roles", roles)
                .issuedAt(Date.from(now.toInstant()))
                .expiration(Date.from(tokenValidity.toInstant()))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public String createRefreshToken(final Long memberId) {
        final ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        final ZonedDateTime tokenValidity = now.plusSeconds(refreshTokenValidityInSeconds);

        return Jwts.builder()
                .claim("id", memberId)
                .issuedAt(Date.from(now.toInstant()))
                .expiration(Date.from(tokenValidity.toInstant()))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public Long getId(final String token) {
        return getClaims(token)
                .getPayload()
                .get("id", Long.class);
    }

    @SuppressWarnings("unchecked")
    public List<RoleType> getRoles(final String token) {
        return getClaims(token)
                .getPayload()
                .get("roles", List.class);
    }

    public void validateToken(final String token) {
        try {
            final Jws<Claims> claims = getClaims(token);
            final Date expiredDate = claims.getPayload().getExpiration();
            final Date now = new Date();

            if (expiredDate.before(now)) {
                throw new AuthException(INVALID_TOKEN);
            }
        } catch (final ExpiredJwtException |
                       SecurityException |
                       MalformedJwtException |
                       UnsupportedJwtException |
                       IllegalArgumentException e) {
            throw new AuthException(INVALID_TOKEN);
        }
    }

    private Jws<Claims> getClaims(final String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
    }
}
