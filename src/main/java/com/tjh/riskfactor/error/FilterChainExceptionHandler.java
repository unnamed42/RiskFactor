package com.tjh.riskfactor.error;

import lombok.val;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * This filter catches all {@code RuntimeException}s in filter chain,
 * so better be put in a very early stage in filter chain, for example
 * before {@code org.springframework.security.web.authentication.logout.LogoutFilter}.
 *
 * But by doing so, the runtime exceptions cannot be auto-translated into corresponding
 * HTTP status codes.
 */
@Component
public class FilterChainExceptionHandler extends GenericFilterBean {

    private final HandlerExceptionResolver resolver;

    public FilterChainExceptionHandler(@Qualifier("handlerExceptionResolver")
                                       HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch(RuntimeException e) {
            val hRequest = (HttpServletRequest)request;
            val hResponse = (HttpServletResponse)response;
            resolver.resolveException(hRequest, hResponse, null, e);
        }
    }
}
