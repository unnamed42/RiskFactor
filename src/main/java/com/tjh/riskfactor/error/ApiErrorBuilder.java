package com.tjh.riskfactor.error;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.stream.Collectors;

import static com.tjh.riskfactor.util.Utils.toJson;

@Component
public class ApiErrorBuilder {

    @Value("${debug}")
    private boolean debug;

    Builder withStatus(HttpStatus status) {
        return new Builder(debug).status(status);
    }

    public static class Builder {

        private final boolean debug;
        private final ApiError error;

        Builder(boolean debug) {
            this.debug = debug;
            this.error = new ApiError();
            this.error.setTimestamp(new Date());
        }

        Builder status(HttpStatus status) {
            error.setStatus(status.value());
            error.setError(status.getReasonPhrase());
            return this;
        }

        Builder request(HttpServletRequest request) {
            error.setUri(request.getRequestURI());
            if(debug) {
                try {
                    error.setBody(request.getReader().lines()
                                    .collect(Collectors.joining(System.lineSeparator())));
                } catch (IllegalStateException | IOException ignored) {}
            }
            return this;
        }

        Builder request(WebRequest request) {
            if(!(request instanceof ServletWebRequest))
                throw new RuntimeException("ApiErrorBuilder supports only HTTP requests");
            final var hRequest = ((ServletWebRequest)request).getRequest();
            return this.request(hRequest);
        }

        Builder message(String message) {
            error.setMessage(message);
            return this;
        }

        Builder exception(Exception ex) {
            if(error.getMessage() == null)
                error.setMessage(ex.getMessage());
            if(debug) {
                final var writer = new StringWriter();
                ex.printStackTrace(new PrintWriter(writer));
                error.setStacktrace(writer.toString());
            }
            return this;
        }

        public String json() {
            return toJson(error).get();
        }

        ResponseEntity<Object> response() {
            final var status = HttpStatus.valueOf(error.getStatus());
            return new ResponseEntity<>(error, status);
        }

    }

}
