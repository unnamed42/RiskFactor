package com.tjh.riskfactor.security;

import lombok.RequiredArgsConstructor;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${security.jwt.signing-key}")
    private String signingKey;

    @Value("${security.jwt.expiry-hours}")
    private Integer expiryHours;

    @Value("${security.jwt.claimed-property}")
    private String claimedProperty;

    private static final String BEARER = "Bearer ";

    private final UserDetailsService userDetailsService;

    public String generateToken(Authentication auth) {
        final var authorities = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        final var expiryMs = expiryHours.longValue() * 3600000L;
        Date now = new Date(), expiry = new Date(now.getTime() + expiryMs);
        return Jwts.builder().setSubject(auth.getName())
                .claim(claimedProperty, authorities)
                .signWith(SignatureAlgorithm.HS256, signingKey)
                .setIssuedAt(now).setExpiration(expiry)
                .setId(((JwtUserDetails)auth.getPrincipal()).getId().toString()).compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
    }

    Authentication getAuthentication(String token) {
        final var username = parseClaims(token).getSubject();
        final var details = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(details, "", details.getAuthorities());
    }

    Optional<String> resolveToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(token -> token.startsWith(BEARER))
                .map(token -> token.substring(BEARER.length()));
    }

    boolean validateToken(String token) {
        return parseClaims(token).getExpiration().after(new Date());
    }

}
