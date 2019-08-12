package com.tjh.riskfactor.controllers;

import lombok.val;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

import com.tjh.riskfactor.entities.ApiError;

@RestControllerAdvice
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
        HttpRequestMethodNotSupportedException ex,
        HttpHeaders headers, HttpStatus status, WebRequest request) {
        val sb = new StringBuilder().append(ex.getMethod())
                        .append(" is not supported on this URI. Supported methods are: [");
        val methods = ex.getSupportedHttpMethods();
        if(methods != null)
            methods.forEach(t -> sb.append(' ').append(t));
        sb.append(" ]");

        return new ApiError().setStatus(HttpStatus.BAD_REQUEST)
                .setMessage(sb.toString()).setUri(request).toResponseEntity();
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        val message = "no handler for method " + ex.getHttpMethod();
        return new ApiError().setStatus(HttpStatus.NOT_FOUND)
                .setMessage(message).setUri(request).toResponseEntity();
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
        HttpMediaTypeNotSupportedException ex,
        HttpHeaders headers, HttpStatus status, WebRequest request) {
        val sb = new StringBuilder().append(ex.getContentType())
                    .append(" media type is not supported. Supported types are: [");
        val types = ex.getSupportedMediaTypes();
        if(types != null)
            types.forEach(t -> sb.append(' ').append(t));
        sb.append(" ]");

        return new ApiError().setStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .setMessage(sb.toString()).setUri(request).toResponseEntity();
    }

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<Object> defaultHandler(Exception ex, HttpServletRequest req) {
        return new ApiError().setStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .setMessage(ex.getMessage()).setUri(req.getRequestURI()).toResponseEntity();
    }

    @ExceptionHandler({ ResponseStatusException.class })
    public ResponseEntity<Object> responseExHandler(ResponseStatusException ex, HttpServletRequest req) {
        return new ApiError().setStatus(ex.getStatus())
                .setMessage(ex.getMessage()).setUri(req.getRequestURI()).toResponseEntity();
    }

}
