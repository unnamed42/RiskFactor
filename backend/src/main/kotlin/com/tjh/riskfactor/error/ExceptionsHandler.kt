package com.tjh.riskfactor.error

import org.springframework.beans.factory.annotation.Autowired

import org.springframework.http.HttpHeaders
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

    @Autowired private lateinit var builder: ApiErrorBuilder

    override fun handleHttpRequestMethodNotSupported(ex: HttpRequestMethodNotSupportedException,
        headers: HttpHeaders, status: HttpStatus, request: WebRequest): ResponseEntity<Any> {
        val message = "${ex.method} is not supported on requested uri. Supported methods are: ${join(ex.supportedHttpMethods)}"
        return builder.withStatus(HttpStatus.BAD_REQUEST).request(request).message(message).exception(ex).response()
    }

    override fun handleHttpMediaTypeNotSupported(ex: HttpMediaTypeNotSupportedException,
        headers: HttpHeaders, status: HttpStatus, request: WebRequest): ResponseEntity<Any> {
        val message = "${ex.contentType} media type is not supported. Supported types are: ${join(ex.supportedMediaTypes)}"
        return builder.withStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE).request(request)
            .message(message).exception(ex).response()
    }

    override fun handleNoHandlerFoundException(ex: NoHandlerFoundException,
        headers: HttpHeaders, status: HttpStatus, request: WebRequest): ResponseEntity<Any> {
        val message = "no handler for method ${ex.httpMethod}"
        return builder.withStatus(HttpStatus.BAD_REQUEST).request(request)
            .message(message).exception(ex).response()
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception, req: HttpServletRequest) =
        builder.withStatus(HttpStatus.INTERNAL_SERVER_ERROR).request(req).exception(ex).response()

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatus(ex: ResponseStatusException, req: HttpServletRequest) =
        builder.withStatus(ex.status).request(req).message(ex.reason ?: "").exception(ex).response()

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(ex: AccessDeniedException, req: HttpServletRequest) =
        builder.withStatus(HttpStatus.FORBIDDEN).request(req).exception(ex).response()

}

private fun join(methods: Iterable<Any>?) =
    "[${methods?.joinToString(", ") { it.toString() } ?: ""}]"
