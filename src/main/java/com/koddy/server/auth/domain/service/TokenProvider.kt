package com.koddy.server.auth.domain.service

import com.koddy.server.auth.exception.AuthException
import com.koddy.server.auth.exception.AuthExceptionCode.INVALID_TOKEN
import com.koddy.server.global.base.DEFAULT_ZONE_ID
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SecurityException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.ZonedDateTime
import java.util.Date
import javax.crypto.SecretKey

@Component
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
        val claims = Jwts.claims()
        claims["id"] = memberId
        claims["authority"] = authority

        // Expires At
        val now = ZonedDateTime.now(DEFAULT_ZONE_ID)
        val tokenValidity = now.plusSeconds(accessTokenValidityInSeconds)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(Date.from(now.toInstant()))
            .setExpiration(Date.from(tokenValidity.toInstant()))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
    }

    fun createRefreshToken(memberId: Long): String {
        // Payload
        val claims = Jwts.claims()
        claims["id"] = memberId

        // Expires At
        val now = ZonedDateTime.now(DEFAULT_ZONE_ID)
        val tokenValidity = now.plusSeconds(refreshTokenValidityInSeconds)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(Date.from(now.toInstant()))
            .setExpiration(Date.from(tokenValidity.toInstant()))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
    }

    fun getId(token: String): Long =
        getClaims(token)
            .body["id"]
            .toString()
            .toLong()

    fun getAuthority(token: String): String =
        getClaims(token)
            .body["authority"]
            .toString()

    fun validateToken(token: String) {
        try {
            val claims: Jws<Claims> = getClaims(token)
            val expiredDate = ZonedDateTime.ofInstant(claims.body.expiration.toInstant(), DEFAULT_ZONE_ID)
            val now = ZonedDateTime.now(DEFAULT_ZONE_ID)

            if (expiredDate < now) {
                throw AuthException(INVALID_TOKEN)
            }
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
}
