package com.tjh.riskfactor.security;

import lombok.val;
import lombok.RequiredArgsConstructor;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tjh.riskfactor.error.ApiError;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider provider;

    private static void reportError(HttpServletRequest request, HttpServletResponse response,
                                    HttpStatus status, String message) throws IOException {
        SecurityContextHolder.clearContext();
        val error = new ApiError().setUri(request).setMessage(message)
                .setStatus(status);
        response.setStatus(status.value());
        response.setHeader("Content-Type", "application/json");
        response.getOutputStream().write(error.toJson().getBytes());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            val auth = provider.resolveToken(request).filter(provider::validateToken)
                        .map(provider::getAuthentication);
            if(auth.isPresent()) {
                SecurityContextHolder.getContext().setAuthentication(auth.get());
                filterChain.doFilter(request, response);
            } else
                reportError(request, response, HttpStatus.UNAUTHORIZED, "token validation failed");
        } catch (IllegalArgumentException | MalformedJwtException e) {
            reportError(request, response, HttpStatus.BAD_REQUEST, "malformed token");
        } catch (ExpiredJwtException | SignatureException e) {
            reportError(request, response, HttpStatus.UNAUTHORIZED, "token validation failed");
        }

    }

}