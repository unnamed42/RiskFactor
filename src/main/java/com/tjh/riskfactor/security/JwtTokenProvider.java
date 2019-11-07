package com.tjh.riskfactor.security;

import lombok.RequiredArgsConstructor;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.Optional;

/**
 * 提供Json Web Token的编解码功能
 * 在标准的JWT当中添加了一个 {@code idt} 的claim，用来表示用户id
 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${security.jwt.signing-key}")
    private String signingKey;

    @Value("${security.jwt.expiry-hours}")
    private Integer expiryHours;

    private static final String BEARER = "Bearer ";

    private final UserDetailsService userDetailsService;

    public String generateToken(Authentication auth) {
        var id = ((JwtUserDetails)auth.getPrincipal()).getId();
        var expiryMs = expiryHours.longValue() * 3600000L;
        var now = new Date();
        var expiry = new Date(now.getTime() + expiryMs);
        return Jwts.builder().setSubject(auth.getName())
                .signWith(key(), SignatureAlgorithm.HS256)
                .setIssuedAt(now).setExpiration(expiry)
                .claim("idt", id).compact();
    }

    private Claims parseClaims(String token) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        return Jwts.parser().setSigningKey(key()).parseClaimsJws(token).getBody();
    }

    Authentication getAuthentication(String token) {
        var username = parseClaims(token).getSubject();
        var details = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(details, "", details.getAuthorities());
    }

    Optional<String> resolveToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(token -> token.startsWith(BEARER))
                .map(token -> token.substring(BEARER.length()));
    }

    boolean validateToken(String token) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        return parseClaims(token).getExpiration().after(new Date());
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(signingKey.getBytes());
    }

}
