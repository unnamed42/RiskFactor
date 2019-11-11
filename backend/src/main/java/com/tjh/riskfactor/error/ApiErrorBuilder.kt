package com.tjh.riskfactor.error

import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest

import javax.servlet.http.HttpServletRequest

import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.Exception
import java.util.Date
import java.util.stream.Collectors

@Component
class ApiErrorBuilder {

    @Value("\${debug}")
    private var debug: Boolean = false

    fun withStatus(status: HttpStatus) = Builder(status)

    inner class Builder(status: HttpStatus) {

        private val error = ApiError(status.value())

        fun request(req: HttpServletRequest): Builder {
            error.uri = req.requestURI
            if(debug) {
                val lines = Iterable { req.reader.lines().iterator() }
                error.body = lines.joinToString(System.lineSeparator())
            }
            return this
        }

        fun request(req: WebRequest): Builder {
            if(req !is ServletWebRequest)
                throw RuntimeException("ApiErrorBuilder supports only HTTP requests")
            return request(req.request)
        }

        fun message(message: String): Builder {
            error.message = message
            return this
        }

        fun exception(e: Exception): Builder {
            if(error.message.isEmpty() && e.message != null)
                error.message = e.message!!
            if(debug) {
                val writer = StringWriter()
                e.printStackTrace(PrintWriter(writer))
                error.stacktrace = writer.toString()
            }
            return this
        }

    }
//
//        public String json() {
//            return toJson(error).get()
//        }
//
//        ResponseEntity<Object> response() {
//            var status = HttpStatus.valueOf(error.getStatus())
//            return new ResponseEntity<>(error, status)
//        }
//
//    }

}
