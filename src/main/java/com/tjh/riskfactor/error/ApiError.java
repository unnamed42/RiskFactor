package com.tjh.riskfactor.error;

import lombok.val;
import lombok.Setter;
import lombok.Getter;
import lombok.experimental.Accessors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;

import java.util.Date;

@Getter
@Accessors(chain = true)
public class ApiError {

    private Date timestamp;
    private Integer status;
    private String error;
    @Setter private String message;
    private String uri;

    public ApiError() {
        this.timestamp = new Date();
    }

    public ApiError setStatus(HttpStatus status) {
        this.status = status.value();
        this.error = status.getReasonPhrase();
        return this;
    }

    public ApiError setUri(String uri) {
        this.uri = uri; return this;
    }

    public ApiError setUri(HttpServletRequest request) {
        return this.setUri(request.getRequestURI());
    }

    public ApiError setUri(WebRequest request) {
        return this.setUri(((ServletWebRequest)request).getRequest());
    }

    public ResponseEntity<Object> toResponseEntity() {
        val status = HttpStatus.valueOf(this.getStatus());
        return new ResponseEntity<>(this, status);
    }

}
