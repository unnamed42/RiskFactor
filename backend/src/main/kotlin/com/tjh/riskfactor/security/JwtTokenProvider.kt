package com.tjh.riskfactor.security

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys

import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

import javax.servlet.http.HttpServletRequest

import java.util.Date

private const val BEARER = "Bearer "

@Component
class JwtTokenProvider(private val userDetailsService: JwtUserDetailsService) {
    @Value("\${security.jwt.signing-key}")
    private lateinit var signingKey: String

    @Value("\${security.jwt.expiry-hours}")
    private lateinit var expiryHours: Number

    fun generateToken(id: Int, username: String): String {
        val expiryMs = expiryHours.toLong() * 3600000L
        val now = Date(); val expiry = Date(now.time + expiryMs)
        return Jwts.builder().setSubject(username)
            .signWith(key(), SignatureAlgorithm.HS256)
            .setIssuedAt(now).setExpiration(expiry)
            .claim("idt", id).compact()
    }

    fun generateToken(auth: Authentication) =
        generateToken((auth.principal as JwtUserDetails).id, auth.name)

    internal fun getAuthentication(token: String): Authentication {
        val username = parseClaims(token).subject
        val details = userDetailsService.loadUserByUsername(username)
        return UsernamePasswordAuthenticationToken(details, "", details.authorities)
    }

    internal fun resolveToken(req: HttpServletRequest): String? =
        req.getHeader("Authorization")?.takeIf { it.startsWith(BEARER) }?.substring(BEARER.length)

    private fun parseClaims(token: String) =
        Jwts.parser().setSigningKey(key()).parseClaimsJws(token).body

    private fun key() = Keys.hmacShaKeyFor(signingKey.toByteArray())

}
