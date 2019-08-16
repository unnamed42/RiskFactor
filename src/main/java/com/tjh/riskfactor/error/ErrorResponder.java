package com.tjh.riskfactor.error;

import lombok.RequiredArgsConstructor;
import lombok.val;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ErrorResponder {

    private final ApiErrorBuilder builder;

    public void response(HttpServletRequest request, HttpServletResponse response,
                         Exception ex, HttpStatus status, String message) throws IOException {
        SecurityContextHolder.clearContext();
        val error = builder.builder().request(request).message(message)
                        .status(status).exception(ex).json();
        response.setStatus(status.value());
        response.setHeader("Content-Type", "application/json");
        response.getOutputStream().write(error.getBytes());
    }

    public void response(HttpServletRequest request, HttpServletResponse response,
                         Exception ex, HttpStatus status) throws IOException {
        this.response(request, response, ex, status, ex.getMessage());
    }

}
