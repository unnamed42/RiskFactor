package com.tjh.riskfactor.security;

import io.jsonwebtoken.*;
import lombok.val;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;

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

    @Value("${security.jwt.claimed-property")
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

    public String extractUsername(String token) {
        return Jwts.parser().setSigningKey(signingKey)
                .parseClaimsJws(token).getBody().getSubject();
    }

    public Authentication getAuthentication(String token) {
        val details = userDetailsService.loadUserByUsername(extractUsername(token));
        return new UsernamePasswordAuthenticationToken(details, "", details.getAuthorities());
    }

    public Optional<String> resolveToken(HttpServletRequest request) {
        return resolveToken(request.getHeader("Authorization"));
    }

    /**
     * Extract JWT from HTTP Authorization header
     * @param bearer content of Authorization header, nullable
     * @return empty if {@code bearer} is null, token string otherwise
     */
    public Optional<String> resolveToken(String bearer) {
        return Optional.ofNullable(bearer)
                .filter(token -> token.startsWith("Bearer "))
                .map(token -> token.substring(7));
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
        } catch(IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "cannot parse content from token");
        } catch(ExpiredJwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "token expired");
        } catch(SignatureException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "authentication failed");
        }
    }

    public boolean validateToken(String token) {
        return validateToken(parseClaims(token));
    }

    public boolean validateToken(Claims claims) {
        return claims.getExpiration().after(new Date());
    }

}
