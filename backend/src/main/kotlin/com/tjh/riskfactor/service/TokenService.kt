package com.tjh.riskfactor.service

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

import org.springframework.stereotype.Service

import java.util.Date
import javax.servlet.http.HttpServletRequest

private const val BEARER = "Bearer "

@Service
class TokenService(
    @Value("\${jwt.signing-key}") signingKey: String,
    @Value("\${jwt.expiry-hours}") private val expiryHours: Int,
    private val service: AccountDetailsService
) {

    private val key = Keys.hmacShaKeyFor(signingKey.toByteArray())
    private val parser = Jwts.parserBuilder().setSigningKey(key).build()

    fun generateToken(id: Int, username: String): String {
        val expiryMs = expiryHours.toLong() * 3600000L
        val now = Date(); val expiry = Date(now.time + expiryMs)
        return Jwts.builder().setSubject(username)
            .signWith(key, SignatureAlgorithm.HS256)
            .setIssuedAt(now).setExpiration(expiry)
            .claim("idt", id).compact()
    }

    fun generateToken(auth: Authentication) =
        generateToken((auth.principal as AccountDetails).id, auth.name)

    fun getAuthentication(token: String): Authentication {
        val username = parser.parseClaimsJws(token).body.subject
        val details = service.loadUserByUsername(username)
        return UsernamePasswordAuthenticationToken(details, "", details.authorities)
    }

    internal fun resolveToken(req: HttpServletRequest): String? =
        req.getHeader("Authorization")?.takeIf { it.startsWith(BEARER) }?.substring(BEARER.length)

}
