package com.koddy.server.auth.domain.service;

import com.koddy.server.auth.exception.AuthException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_TOKEN;

@Component
public class TokenProvider {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

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

    public String createAccessToken(final long memberId, final String authority) {
        // Payload
        final Claims claims = Jwts.claims();
        claims.put("id", memberId);
        claims.put("authority", authority);

        // Expires At
        final ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        final ZonedDateTime tokenValidity = now.plusSeconds(accessTokenValidityInSeconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(tokenValidity.toInstant()))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(final long memberId) {
        // Payload
        final Claims claims = Jwts.claims();
        claims.put("id", memberId);

        // Expires At
        final ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        final ZonedDateTime tokenValidity = now.plusSeconds(refreshTokenValidityInSeconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(tokenValidity.toInstant()))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public long getId(final String token) {
        return getClaims(token)
                .getBody()
                .get("id", Long.class);
    }

    public String getAuthority(final String token) {
        return getClaims(token)
                .getBody()
                .get("authority", String.class);
    }

    public void validateToken(final String token) {
        try {
            final Jws<Claims> claims = getClaims(token);
            final Date expiredDate = claims.getBody().getExpiration();
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
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
    }
}
