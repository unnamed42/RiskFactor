package com.tjh.riskfactor.security;

import lombok.RequiredArgsConstructor;
import lombok.val;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends GenericFilterBean {

    private final JwtTokenProvider provider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain filterChain) throws IOException, ServletException {
        val auth = provider.resolveToken((HttpServletRequest)request)
                    .filter(provider::validateToken)
                    .map(provider::getAuthentication);
        SecurityContextHolder.getContext().setAuthentication(auth.orElse(null));

        filterChain.doFilter(request, response);
    }

}