package com.tjh.riskfactor.security;

import lombok.val;
import lombok.RequiredArgsConstructor;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;

import com.fasterxml.jackson.core.JsonProcessingException;

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

import static com.tjh.riskfactor.util.Utils.kvMap;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${security.jwt.signing-key}")
    private String signingKey;

    @Value("${security.jwt.expiry-hours}")
    private Integer expiryHours;

    @Value("${security.jwt.claimed-property}")
    private String claimedProperty;

    private final UserDetailsService userDetailsService;

    public String generateToken(Authentication auth) {
        val authorities = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        val expiryMs = expiryHours.longValue() * 3600000L;
        Date now = new Date(), expiry = new Date(now.getTime() + expiryMs);
        return Jwts.builder().setSubject(auth.getName())
                .claim(claimedProperty, authorities)
                .signWith(SignatureAlgorithm.HS256, signingKey)
                .setIssuedAt(now).setExpiration(expiry).compact();
    }

    public Optional<String> tokenToJson(String token) {
        val claims = parseClaims(token);
        return kvMap().add("username", claims.getSubject())
                .add(claimedProperty, claims.get(claimedProperty))
                .add("issued_at", claims.getIssuedAt())
                .add("expire_at", claims.getExpiration()).buildJson();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
    }

    Authentication getAuthentication(String token) {
        val username = parseClaims(token).getSubject();
        val details = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(details, "", details.getAuthorities());
    }

    public Optional<String> resolveToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(token -> token.startsWith("Bearer "))
                .map(token -> token.substring(7));
    }

    boolean validateToken(String token) {
        return parseClaims(token).getExpiration().after(new Date());
    }

}
