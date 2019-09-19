package com.tjh.riskfactor.security;

import lombok.RequiredArgsConstructor;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tjh.riskfactor.error.ErrorResponder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider provider;
    private final ErrorResponder e;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            provider.resolveToken(request).filter(provider::validateToken)
                    .map(provider::getAuthentication)
                    .ifPresent(auth -> SecurityContextHolder.getContext().setAuthentication(auth));
            filterChain.doFilter(request, response);
        } catch (IllegalArgumentException | MalformedJwtException ex) {
            e.response(request, response, ex, HttpStatus.UNAUTHORIZED, "malformed token");
        } catch (ExpiredJwtException | SignatureException ex) {
            e.response(request, response, ex, HttpStatus.UNAUTHORIZED, "token validation failed");
        }

    }

}
