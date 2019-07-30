package com.tjh.riskfactor.controllers;

import lombok.val;
import lombok.Getter;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;

import java.util.Date;

@RestControllerAdvice
public class ExceptionAdvice {

    @Getter @AllArgsConstructor
    private static class Error {
        private Date timestamp;
        private String message;
        private String uri;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> defaultHandler(Exception ex, HttpServletRequest req) {
        val error = new Error(new Date(), ex.getMessage(), req.getRequestURI());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Error> responseExHandler(ResponseStatusException ex, HttpServletRequest req) {
        val error = new Error(new Date(), ex.getMessage(), req.getRequestURI());
        return new ResponseEntity<>(error, ex.getStatus());
    }

}
