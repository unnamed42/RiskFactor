package com.tjh.riskfactor.api.error

import com.fasterxml.jackson.databind.ObjectMapper

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import org.springframework.security.core.context.SecurityContextHolder

import com.tjh.riskfactor.common.applyIf

import java.io.PrintWriter
import java.io.StringWriter
import java.lang.IllegalStateException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestControllerAdvice
class ErrorHandler(
    private val builder: ApiErrorBuilder,
    private val mapper: ObjectMapper
): ResponseEntityExceptionHandler() {

    override fun handleHttpRequestMethodNotSupported(ex: HttpRequestMethodNotSupportedException,
                                                     headers: HttpHeaders, status: HttpStatus, request: WebRequest): ResponseEntity<Any> {
        val message = "${ex.method} is not supported on requested uri. Supported methods are: ${join(ex.supportedHttpMethods)}"
        return builder.error(status).request(request).message(message).exception(ex).build()
    }

    override fun handleHttpMediaTypeNotSupported(ex: HttpMediaTypeNotSupportedException,
                                                 headers: HttpHeaders, status: HttpStatus, request: WebRequest): ResponseEntity<Any> {
        val message = "${ex.contentType} media type is not supported. Supported types are: ${join(ex.supportedMediaTypes)}"
        return builder.error(status).request(request).message(message).exception(ex).build()
    }

    override fun handleNoHandlerFoundException(ex: NoHandlerFoundException,
                                               headers: HttpHeaders, status: HttpStatus, request: WebRequest): ResponseEntity<Any> {
        val message = "no handler for method ${ex.httpMethod}"
        return builder.error(status).request(request).message(message).exception(ex).build()
    }

    fun respondException(req: HttpServletRequest, resp: HttpServletResponse, ex: Exception, status: HttpStatus) {
        SecurityContextHolder.clearContext()
        val body = builder.error(status).request(req).message(ex.message ?: "").exception(ex).entity
        resp.status = status.value()
        resp.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
        resp.outputStream.write(mapper.writeValueAsBytes(body))
        resp.flushBuffer()
    }

    @ExceptionHandler(value = [
        Exception::class,
        ResponseStatusException::class,
        AccessDeniedException::class])
    fun handleException(ex: Exception, req: HttpServletRequest): ResponseEntity<Any> = when(ex) {
        is ResponseStatusException -> builder.error(ex.status).message(ex.reason ?: "")
        is AccessDeniedException -> builder.error(HttpStatus.FORBIDDEN)
        else -> builder.error(HttpStatus.INTERNAL_SERVER_ERROR)
    }.request(req).exception(ex).build()

    private fun join(args: Iterable<Any>?): String =
        "[${args?.joinToString(", ") { it.toString() } ?: ""}]"

}

data class ApiError(
    val status: HttpStatus,
    var error: String = "",
    var message: String = "",
    var uri: String = "",
    var body: String? = null,
    var stacktrace: String? = null,
    var timestamp: Long = System.currentTimeMillis()
)

@Component
class ApiErrorBuilder(@Value("\${debug}") private val debug: Boolean) {

    fun error(status: HttpStatus) = Builder(status)

    inner class Builder(status: HttpStatus) {

        val entity: ApiError = ApiError(status)

        fun request(req: WebRequest): Builder {
            if(req !is ServletWebRequest)
                throw RuntimeException("ApiErrorBuilder supports only HTTP requests")
            return request(req.request)
        }

        fun request(req: HttpServletRequest): Builder = this.apply { entity.uri = req.requestURI }.applyIf(debug) {
            entity.body = try { req.inputStream.bufferedReader().use { it.readText() } } catch (e: IllegalStateException) { null }
        }

        fun exception(ex: Exception): Builder = this.applyIf(debug) {
            entity.stacktrace = StringWriter().also { ex.printStackTrace(PrintWriter(it)) }.toString()
        }

        fun message(msg: String): Builder = this.apply { entity.message = msg }

        fun build() = ResponseEntity<Any>(entity, entity.status)
    }

}
