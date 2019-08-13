package com.tjh.riskfactor.security;

import lombok.val;
import lombok.RequiredArgsConstructor;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${security.jwt.signing-key}")
    private String signingKey;

    @Value("${security.jwt.expiry-ms}")
    private Integer expiryMs;

    private final UserDetailsService userDetailsService;

    public String generateToken(String username, List<String> roles) {
        val claims = Jwts.claims().setSubject(username);
        claims.put("roles", roles);
        Date now = new Date(), expiry = new Date(now.getTime() + expiryMs);
        return Jwts.builder().setClaims(claims)
                .setIssuedAt(now).setExpiration(expiry)
                .signWith(SignatureAlgorithm.HS256, signingKey)
                .compact();
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
        val token = request.getHeader("Authorization");
        if(token != null && token.startsWith("Bearer "))
            return Optional.of(token.substring(7));
        return Optional.empty();
    }

    public boolean validateToken(String token) {
        val claims = Jwts.parser().setSigningKey(signingKey)
                        .parseClaimsJws(token);
        return claims.getBody().getExpiration().before(new Date());
    }

}
