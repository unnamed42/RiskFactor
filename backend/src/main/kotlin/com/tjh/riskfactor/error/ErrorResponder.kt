package com.tjh.riskfactor.error

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class ErrorResponder {

    @Autowired private lateinit var builder: ApiErrorBuilder

    fun response(req: HttpServletRequest, resp: HttpServletResponse,
                 ex: Exception, status: HttpStatus, message: String) {
        SecurityContextHolder.clearContext()
        val error = builder.withStatus(status).request(req)
            .message(message).exception(ex).json()
        resp.status = status.value()
        resp.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
        resp.outputStream.write(error.toByteArray())
    }

    fun response(req: HttpServletRequest, resp: HttpServletResponse,
                 ex: Exception, status: HttpStatus) =
        response(req, resp, ex, status, ex.message ?: "")

}
