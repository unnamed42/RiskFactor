package com.tjh.riskfactor.error

import lombok.RequiredArgsConstructor

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.context.request.WebRequest
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

import javax.servlet.http.HttpServletRequest

@RestControllerAdvice
class ExceptionsHandler: ResponseEntityExceptionHandler() {

    override fun handleHttpRequestMethodNotSupported(ex: HttpRequestMethodNotSupportedException,
        headers: HttpHeaders, status: HttpStatus, request: WebRequest): ResponseEntity<Any> {
        val message = "${ex.method} is not supported on requested uri. Supported methods are: ${join(ex.supportedHttpMethods)}"
        return super.handleHttpRequestMethodNotSupported(ex, headers, status, request)
    }

}

private fun join(methods: Iterable<HttpMethod>?) =
    "[${methods?.joinToString(", ") { it.name } ?: ""}]"

//
//@RestControllerAdvice
//@RequiredArgsConstructor
//public class ExceptionsHandler extends ResponseEntityExceptionHandler {
//
//    private final ApiErrorBuilder builder;
//
//    @Override
//    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
//        HttpRequestMethodNotSupportedException ex,
//        HttpHeaders headers, HttpStatus status, WebRequest request) {
//        var message = ex.getMethod() +
//                " is not supported on requested uri. Supported methods are: " +
//                join(ex.getSupportedHttpMethods());
//        return builder.withStatus(HttpStatus.BAD_REQUEST)
//                .request(request).message(message).exception(ex).response();
//    }
//
//    @Override
//    protected ResponseEntity<Object> handleNoHandlerFoundException(
//            NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
//        var message = "no handler for method " + ex.getHttpMethod();
//        return builder.withStatus(HttpStatus.BAD_REQUEST)
//                .request(request).message(message).exception(ex).response();
//    }
//
//    @Override
//    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
//        HttpMediaTypeNotSupportedException ex,
//        HttpHeaders headers, HttpStatus status, WebRequest request) {
//        var message = ex.getContentType() +
//                " media type is not supported. Supported types are: " +
//                join(ex.getSupportedMediaTypes());
//        return builder.withStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
//                .request(request).message(message).exception(ex).response();
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<Object> handleDefault(Exception ex, HttpServletRequest req) {
//        return builder.withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//                .request(req).exception(ex).response();
//    }
//
//    @ExceptionHandler(ResponseStatusException.class)
//    public ResponseEntity<Object> handleResponse(ResponseStatusException ex, HttpServletRequest req) {
//        return builder.withStatus(ex.getStatus())
//                .request(req).message(ex.getReason()).exception(ex).response();
//    }
//
//    @ExceptionHandler(AccessDeniedException.class)
//    public ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
//        return builder.withStatus(HttpStatus.FORBIDDEN)
//                .request(req).exception(ex).response();
//    }
//
//}
