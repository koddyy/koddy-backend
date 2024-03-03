package com.koddy.server.auth.domain.service

import com.koddy.server.auth.exception.AuthException
import com.koddy.server.auth.exception.AuthExceptionCode.INVALID_TOKEN
import com.koddy.server.global.base.DEFAULT_ZONE_ID
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Header
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SecurityException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.time.ZonedDateTime
import java.util.Date
import java.util.UUID
import javax.crypto.SecretKey

@Service
class TokenProvider(
    @Value("\${jwt.secret-key}") secretKey: String,
    @Value("\${jwt.access-token-validity-seconds}") private val accessTokenValidityInSeconds: Long,
    @Value("\${jwt.refresh-token-validity-seconds}") private val refreshTokenValidityInSeconds: Long,
) {
    private val secretKey: SecretKey = Keys.hmacShaKeyFor(secretKey.toByteArray(StandardCharsets.UTF_8))

    fun createAccessToken(
        memberId: Long,
        authority: String,
    ): String {
        // Payload
        val claims: Claims = Jwts.claims()
        claims["id"] = memberId
        claims["authority"] = authority

        // Expires At
        val now: ZonedDateTime = ZonedDateTime.now(DEFAULT_ZONE_ID)
        val tokenValidity: ZonedDateTime = now.plusSeconds(accessTokenValidityInSeconds)

        return createToken(
            subject = ACCESS_TOKEN_SUBJECT,
            claims = claims,
            issuedAt = Date.from(now.toInstant()),
            expiration = Date.from(tokenValidity.toInstant()),
        )
    }

    fun createRefreshToken(memberId: Long): String {
        // Payload
        val claims: Claims = Jwts.claims()
        claims["id"] = memberId

        // Expires At
        val now: ZonedDateTime = ZonedDateTime.now(DEFAULT_ZONE_ID)
        val tokenValidity: ZonedDateTime = now.plusSeconds(refreshTokenValidityInSeconds)

        return createToken(
            subject = REFRESH_TOKEN_SUBJECT,
            claims = claims,
            issuedAt = Date.from(now.toInstant()),
            expiration = Date.from(tokenValidity.toInstant()),
        )
    }

    private fun createToken(
        subject: String,
        claims: Claims,
        issuedAt: Date,
        expiration: Date,
    ): String =
        Jwts.builder()
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            .setClaims(claims)
            .setIssuer(ISSUER)
            .setSubject(subject)
            .setIssuedAt(issuedAt)
            .setExpiration(expiration)
            .setNotBefore(issuedAt)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .setId(UUID.randomUUID().toString())
            .compact()

    fun getId(token: String): Long =
        getClaims(token)
            .body["id"]
            .toString()
            .toLong()

    fun getAuthority(token: String): String =
        getClaims(token)
            .body["authority"]
            .toString()

    fun validateAccessToken(token: String) = validateToken(token, ACCESS_TOKEN_SUBJECT)

    fun validateRefreshToken(token: String) = validateToken(token, REFRESH_TOKEN_SUBJECT)

    private fun validateToken(
        token: String,
        subject: String,
    ) {
        try {
            val claims = getClaims(token)
            val payload = claims.body
            checkExpiration(payload)
            checkIssuer(payload)
            checkSubject(payload, subject)
        } catch (e: ExpiredJwtException) {
            throw AuthException(INVALID_TOKEN)
        } catch (e: SecurityException) {
            throw AuthException(INVALID_TOKEN)
        } catch (e: MalformedJwtException) {
            throw AuthException(INVALID_TOKEN)
        } catch (e: UnsupportedJwtException) {
            throw AuthException(INVALID_TOKEN)
        } catch (e: IllegalArgumentException) {
            throw AuthException(INVALID_TOKEN)
        }
    }

    private fun getClaims(token: String): Jws<Claims> =
        Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)

    private fun checkExpiration(payload: Claims) {
        val expiredDate: ZonedDateTime = ZonedDateTime.ofInstant(payload.expiration.toInstant(), DEFAULT_ZONE_ID)
        val now: ZonedDateTime = ZonedDateTime.now(DEFAULT_ZONE_ID)
        if (expiredDate < now) {
            throw AuthException(INVALID_TOKEN)
        }
    }

    private fun checkIssuer(payload: Claims) {
        if (ISSUER != payload.issuer) {
            throw AuthException(INVALID_TOKEN)
        }
    }

    private fun checkSubject(
        payload: Claims,
        subject: String,
    ) {
        if (subject != payload.subject) {
            throw AuthException(INVALID_TOKEN)
        }
    }

    companion object {
        private const val ISSUER: String = "Koddy"
        private const val ACCESS_TOKEN_SUBJECT: String = "Auth"
        private const val REFRESH_TOKEN_SUBJECT: String = "Reissue"
    }
}
