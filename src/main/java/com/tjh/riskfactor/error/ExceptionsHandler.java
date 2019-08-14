package com.tjh.riskfactor.error;

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

import java.util.Collection;

@RestControllerAdvice
public class ExceptionsHandler extends ResponseEntityExceptionHandler {

    private String join(Collection<?> list) {
        val sb = new StringBuilder().append('[');
        if(list != null)
            list.forEach(item -> sb.append(' ').append(item));
        sb.append(" ]");
        return sb.toString();
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
        HttpRequestMethodNotSupportedException ex,
        HttpHeaders headers, HttpStatus status, WebRequest request) {
        val message = ex.getMethod() +
                " is not supported on requested uri. Supported methods are: " +
                join(ex.getSupportedHttpMethods());
        return new ApiError().setStatus(HttpStatus.BAD_REQUEST)
                .setMessage(message).setUri(request).toResponseEntity();
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        val message = "no handler for method " + ex.getHttpMethod();
        return new ApiError().setStatus(HttpStatus.BAD_REQUEST)
                .setMessage(message).setUri(request).toResponseEntity();
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
        HttpMediaTypeNotSupportedException ex,
        HttpHeaders headers, HttpStatus status, WebRequest request) {
        val message = ex.getContentType() +
                " media type is not supported. Supported types are: " +
                join(ex.getSupportedMediaTypes());
        return new ApiError().setStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .setMessage(message).setUri(request).toResponseEntity();
    }

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<Object> defaultHandler(Exception ex, HttpServletRequest req) {
        return new ApiError().setStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .setMessage(ex.getMessage()).setUri(req).toResponseEntity();
    }

    @ExceptionHandler({ ResponseStatusException.class })
    public ResponseEntity<Object> responseExHandler(ResponseStatusException ex, HttpServletRequest req) {
        return new ApiError().setStatus(ex.getStatus())
                .setMessage(ex.getReason()).setUri(req).toResponseEntity();
    }

}
